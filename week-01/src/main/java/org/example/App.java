package org.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Class h = new FsClassLoader().loadClass("Hello");

        Object obj = h.newInstance();
        Method hello = h.getMethod("hello");
        hello.invoke(obj); // Hello, classLoader!
    }
}
