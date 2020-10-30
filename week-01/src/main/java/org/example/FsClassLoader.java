package org.example;

import java.io.*;

public class FsClassLoader extends ClassLoader {

    /**
     * 类文件所在路径
     */
    private final String EXTENDS_PATH = this.getClass().getResource("/").getPath() + "extClass/";

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 遵循双亲委派策略，优先使用父加载器加载类
        Class c = superfindClassIgnoringNotFound(name);
        if (c != null) {
            return c;
        }
        // 类限定名转类路径
        String classPath = classNameToPath(name);
        // 获取该class文件字节码数组
        byte[] classData = loadClassFile(name, classPath);
        // 对class文件字节码进行解码
        classData = decodeClassByte(classData);
        if (classData != null) {
            // 将class的字节码数组转换成Class类的实例
            c = defineClass(name, classData, 0, classData.length);
        }
        return c;
    }

    /**
     * 加载类文件
     *
     * @param name
     * @param classPath
     * @return
     * @throws ClassNotFoundException
     */
    private byte[] loadClassFile(String name, String classPath) throws ClassNotFoundException {
        // 参考tomcat的类加载器限制，单个class文件最大为2MB
        File originFile = new File(EXTENDS_PATH + classPath);
        if (!originFile.exists() || originFile.length() < 0 || originFile.length() > 2048) {
            throw new ClassNotFoundException(name);
        }
        try (FileInputStream fileInputStream = new FileInputStream(originFile)) {
            byte[] originByteArr = new byte[(int) originFile.length()];
            // 读取数组
            fileInputStream.read(originByteArr);
            return originByteArr;
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        }
    }

    /**
     * 调用父加载器加载类文件,并忽略ClassNotFound异常
     *
     * @param name
     * @return
     */
    private Class<?> superfindClassIgnoringNotFound(String name) {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException var3) {
            return null;
        }
    }

    /**
     * 解码从文件中加载的原始class数据
     *
     * @param originByteArr
     * @return
     */
    private byte[] decodeClassByte(byte[] originByteArr) {
        for (int i = 0; i < originByteArr.length; i++) {
            originByteArr[i] = (byte)(255 - originByteArr[i]);
        }
        return originByteArr;
    }

    /**
     * 类限定名转类路径
     *
     * @param className
     * @return
     */
    private String classNameToPath(String className) {
        StringBuilder path = new StringBuilder(6 + className.length());
        path.append(className.replace('.', '/'));
        path.append(".xlass");
        return path.toString();
    }
}
