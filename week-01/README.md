### 题目
题目地址:  https://fshows.yuque.com/tech-ozd0u/bgaw8p/omzoyo

### 第一题
代码地址: https://github.com/liluqing/deep-in-java/blob/main/week-01/src/main/java/org/example/FsClassLoader.java

### 第二题
```
2020-10-29T21:19:19.488+0800: 114.015: [GC (CMS Initial Mark) [1 CMS-initial-mark: 106000K(2097152K)] 1084619K(3984640K), 0.2824583 secs] [Times: user=0.86 sys=0.00, real=0.28 secs]
```

* 2020-10-29T21:19:19.488+0800  当前gc操作的起始时间
* 114.015   jvm运行时间，单位（秒），代表jvm启动后114.015秒时触发了当前GC操作
* GC (CMS Initial Mark) ： 表示使用CMS垃圾收集器进行收集，当前为**初始标记阶段**。在该阶段中垃圾收集器会开始扫描并标记所有与“GC Roots ”直接关联的对象，该阶段会触发stop the word。
* [1 CMS-initial-mark: 106000K(2097152K)]：表示老年代空间使用情况，其中老年代空间总大小为“2097152K”，已使用老年代空间“106000K”。通过计算“106000/2097152=0.05”，当前CMS收集器的启动内存使用阀值为0.05，**说明jvm参数配置可能存在问题**。
* 1084619K(3984640K), :  表示当前堆内存使用情况，整堆大小“3984640K”，当前使用“1084619K”。
* 0.2824583 secs] ： 表示本次CMS GC 初始标记阶段总耗时282毫秒, 需注意初始标记阶段期间会触发STW。
* [Times: user=0.86 sys=0.00, real=0.28 secs]: 表示当前阶段所消耗的用户态CPU时间、内核态CPU时间、以及真实耗时。由于当前阶段是多线程并发标记，故user time和sys time可能会大于real time

```
2020-10-29T21:19:19.771+0800: 114.298: [CMS-concurrent-mark-start]
```

* CMS-concurrent-mark-start：CMS GC并发标记阶段开始，在本阶段中，GC线程会和用户线程同时运行，GC线程会遍历并标记整个老年代中存活的对象，在此期间，如果被标记的对象引用发生改变，比如“引用被用户线程改变”，“新生代晋升到老年代”，“对象被直接分配在老年代”等情况，jvm就会将这些对象所在的card标记为dirty，

```
2020-10-29T21:19:19.931+0800: 114.458: [CMS-concurrent-mark: 0.160/0.160 secs] [Times: user=0.32 sys=0.03, real=0.16 secs]
```

* CMS-concurrent-mark: 0.160/0.160 secs:  表示并发标记阶段，实际耗1.6秒，用户态cup时间0.32秒，内核态cpu时间0.16秒。

```
2020-10-29T21:19:19.931+0800: 114.459: [CMS-concurrent-preclean-start]
```

* CMS-concurrent-preclean-start :  预清理阶段开始，该阶段会把上一阶段中被标记为dirty的card的区域的对象重新进行遍历。

```
2020-10-29T21:19:19.998+0800: 114.525: [CMS-concurrent-preclean: 0.065/0.066 secs] [Times: user=0.05 sys=0.01, real=0.06 secs]
```

* CMS-concurrent-preclean: 0.065/0.066 secs:  表示"预清理阶段"的持续时间和时钟时间

* Times: user=0.05 sys=0.01, real=0.06 secs: 表示用户态cpu时间、内核态cpu时间和实际耗时

```
2020-10-29T21:19:19.998+0800: 114.525: [CMS-concurrent-abortable-preclean-start]CMS: abort preclean due to time 
```

*  CMS-concurrent-abortable-preclean-start: 可终止的并发预清理, 这个阶段会尽可能的尝试去承担后面final remark的工作，减少cms remark的耗时。GC线程标记对象的速度是不可能跟上对象引用改变的速度，所以当前阶段存在一些终止条件，主要的终止条件有重复次数，多少量的工作，持续时间等，此阶段中一般最大的持续时间为5秒。达到终止条件则当前阶段结束。

```
2020-10-29T21:19:25.072+0800: 119.599: [CMS-concurrent-abortable-preclean: 5.038/5.073 secs] [Times: user=7.72 sys=0.50, real=5.08 secs]
```

* CMS-concurrent-abortable-preclean: 5.038/5.073 secs：CMS-concurrent-abortable-preclean阶段的持续时间和时钟时间
* Times: user=7.72 sys=0.50, real=5.08 secs：CMS-concurrent-abortable-preclean阶段的用户态cpu耗时、内核态cpu耗时和实际耗时

```
2020-10-29T21:19:25.076+0800: 119.603: [GC (CMS Final Remark) [YG occupancy: 1279357 K (1887488 K)]
```

* GC (CMS Final Remark): CMS 重新标记阶段，该阶段会触发STW。在该阶段中会完成标记整个老年代的所有对象，该阶段被细分成下面的读个子阶段
* YG occupancy: 1279357 K (1887488 K)：描述当前年轻代内存使用情况，当前年轻代内存已使用内存“1279357 K ”，年轻带总内存大小“1887488 K”

```
2020-10-29T21:19:25.076+0800: 119.603: [Rescan (parallel) , 0.3120602 secs]
```

* Rescan (parallel) , 0.3120602 secs : remark子阶段- 重新扫描并标记堆中所有存活的对象，parallel表示多线程并发扫描，总耗时0.31秒

```
2020-10-29T21:19:25.388+0800: 119.915: [weak refs processing, 0.0015920 secs]
```

* [weak refs processing, 0.0015920 secs]   remark子阶段- 处理弱应用对象，耗时：0.001秒

```
2020-10-29T21:19:25.390+0800: 119.917: [class unloading, 0.0517863 secs]
```

* [class unloading, 0.0517863 secs] :   remark子阶段 - 卸载无用的class，耗时 0.051秒

```
2020-10-29T21:19:25.441+0800: 119.969: [scrub symbol table, 0.0212825 secs]
```

* [scrub symbol table, 0.0212825 secs] :  remar子阶段 - 清理符号表，耗时0.21秒

```
2020-10-29T21:19:25.463+0800: 119.990: [scrub string table, 0.0022435 secs][1 CMS-remark: 106000K(2097152K)] 1385358K(3984640K), 0.3959182 secs] [Times: user=1.33 sys=0.00, real=0.40 secs]
```

* scrub string table, 0.0022435 secs ： remar子阶段 - 清理内部化字符串的符号表和字符串表 ，耗时0.002秒
* 1 CMS-remark: 106000K(2097152K)  ： 当前阶段老年代使用情况，当前老年代已使用空间“106000K”，老年代总空间“2097152K”
* 1385358K(3984640K), 0.3959182 secs ： 描述remark阶段后整个堆的内存使用情况，已使用堆内存“1385358K”，总堆内存“3984640K”，本次remark阶段总耗时0.395秒
* [Times: user=1.33 sys=0.00, real=0.40 secs]： 表用户态cpu时间、内核态cpu时间和实际耗时

```
2020-10-29T21:19:25.473+0800: 120.000: [CMS-concurrent-sweep-start]
```

*  [CMS-concurrent-sweep-start] :   并发清除阶段开始启动，该阶段可以和用户线程并发执行，不会触发STW

```
2020-10-29T21:19:25.540+0800: 120.067: [CMS-concurrent-sweep: 0.067/0.067 secs] [Times: user=0.18 sys=0.02, real=0.06 secs]
```

*  [CMS-concurrent-sweep: 0.067/0.067 secs] ： 并发清除--第一个子阶段，该阶段主要是清除那些没有标记的无用对象并回收内存。本阶段耗时0.67秒

```
2020-10-29T21:19:25.540+0800: 120.068: [CMS-concurrent-reset-start]
2020-10-29T21:19:25.544+0800: 120.071: [CMS-concurrent-reset: 0.003/0.003 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
```

* [CMS-concurrent-reset-start] ： 并发清除--第二个子阶段，在该阶段GC会重新设置CMS算法内部的数据结构，准备下一次触发CMS GC做准备

### 第三题

##### 启动参数

```
java -Denv=PRO 
        -server 
        -Xms4g 
        -Xmx4g 
        -Xmn2g 
        -XX:MaxDirectMemorySize=512m 
        -XX:MetaspaceSize=128m 
        -XX:MaxMetaspaceSize=512m 
        -XX:-UseBiasedLocking 
        -XX:-UseCounterDecay 
        -XX:AutoBoxCacheMax=10240 
        -XX:+UseConcMarkSweepGC 
        -XX:CMSInitiatingOccupancyFraction=75 
        -XX:+UseCMSInitiatingOccupancyOnly 
        -XX:MaxTenuringThreshold=6 
        -XX:+ExplicitGCInvokesConcurrent 
        -XX:+ParallelRefProcEnabled 
        -XX:+PerfDisableSharedMem 
        -XX:+AlwaysPreTouch 
        -XX:-OmitStackTraceInFastThrow  
        -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled 
        -XX:+HeapDumpOnOutOfMemoryError 
        -XX:HeapDumpPath=/home/devjava/logs/ 
        -Xloggc:/home/devjava/logs/lifecircle-tradecore-gc.log 
        -XX:+PrintGCApplicationStoppedTime 
        -XX:+PrintGCDateStamps 
        -XX:+PrintGCDetails 
        -javaagent:/home/devjava/ArmsAgent/arms-bootstrap-1.7.0-SNAPSHOT.jar 
        -jar /home/devjava/lifecircle-tradecore/app/lifecircle-tradecore.jar
```

* -Xms4g  -Xmx4g ：

  jvm堆内存的初始大小和最大堆内存限制，一般设置成一样，在jvm启动时一次性申请所需内存，避免之后反复申请，避免操作系统内存不足申请失败的情况。

  

* -Xmn2g：

  年轻代内存大小，默认为堆内存的1/3，

  

* -XX:MaxDirectMemorySize=512m：

  最直接内存大小,默认为堆内存-survicor区的大小

  

* -XX:MetaspaceSize=128m  -XX:MaxMetaspaceSize=512m  ： 

  元数据区大小，初始空间128MB，最大512MB。如果超出限制则会触发GC

  

* -XX:-UseBiasedLocking： 

  禁用偏向锁，在高并发，锁竞争激烈，或者存在大量锁对象创建的应用中，应禁用偏向锁以优化性能

  

* -XX:-UseCounterDecay： 

* 关闭JIT即时编译的热衰减统计，转而让方法统计计数器统计方法的绝对调用次数，绝对调用次数server模式下默认为1000次，也可通过“-XX:CompileThreshold=”参数指定。（方法热衰减统计和绝对值统计是JIT确定那些方法可以被编译成本地代码的两种策略）

  

* -XX:AutoBoxCacheMax=10240 ： 

  jdk自动装箱的默认缓存范围（即Integer和Long的缓存），默认为-128~127，超出范围会创建对象

  

* -XX:+UseConcMarkSweepGC：

  启用CMS垃圾收集器

  

* -XX:CMSInitiatingOccupancyFraction=75：

  cms老年代出发GC的默认使用率,默认值为92%，该参数必须配合“ -XX:+UseCMSInitiatingOccupancyOnly  ”参数使用才有效

  

* -XX:+UseCMSInitiatingOccupancyOnly 

  是否手动设定老年代的回收阈值，需配合“-XX:CMSInitiatingOccupancyFraction=”参数使用。

  

* -XX:MaxTenuringThreshold=6 

  survivor区对象晋升老年代的对象年龄阀值，以“-XX:MaxTenuringThreshold=6 ”为例survivor区对象经历过6次youngGC后则晋升到老年代

  

*  -XX:+ExplicitGCInvokesConcurrent  ：

  使用System.gc()时触发CMS GC，而不是Full GC。默认不开启

  

* -XX:+ParallelRefProcEnabled  

  启用并行的处理Reference对象，如应用有大量的引用或者finalizable对象需要处理，则启用该配置

  

*  -XX:+PerfDisableSharedMem

  启用标准内存使用。JVM控制分为标准或共享内存，区别在于一个是在JVM内存中，一个是生成/tmp/hsperfdata_{userid}/{pid}文件，存储统计数据，通过mmap映射到内存中，别的进程可以通过文件访问内容。通过这个参数，可以禁止JVM写在文件中写统计数据，代价就是jps、jstat这些命令用不了了，只能通过jmx获取数据

  

* -XX:+AlwaysPreTouch 

  给java堆预分配物理内存，可保证分配到的内存页是连续的。新生代对象晋升时不会因为申请内存使GC停顿加长

  

* -XX:-OmitStackTraceInFastThrow  

  不忽略重复异常的栈，这是JDK的优化，大量重复的JDK异常不再打印其StackTrace。如果系统长时间运行，在同一个方法上运行了多次，
         

* -XX:+HeapDumpOnOutOfMemoryError  

  当发生内存溢出时自动dump出内存映像到磁盘指定位置，需配置“ -XX:HeapDumpPath”配置使用
       

* -XX:HeapDumpPath=/home/devjava/logs/  

  指定当内存溢出时自动dump出的内存映像文件的磁盘位置        

  

* -XX:+PrintGCDetails  

  ​        启用gc日志打印功能

  

* -Xloggc:/home/devjava/logs/lifecircle-tradecore-gc.log  

  指定GC日志的保存路径
         

* -XX:+PrintGCApplicationStoppedTime  

  在gc日志中打印垃圾收集时 , jvm的停顿时间

  ​         

* -XX:+PrintGCDateStamps  

  在gc日志中打印可读的日期而不是时间戳




