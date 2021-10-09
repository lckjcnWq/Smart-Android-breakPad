# library_breakPad
用于检测navtive异常的谷歌breakpad library库


## :-: **native Crash详细定位出错行**

### **一.步骤**
1、直接引用已经编译好的library库  
~~~
mavenCentral()

implementation 'io.github.lckjcnWq:breakpad:1.0.0'
~~~


2、当native有异常，会自动生成利.dump文件。  
3、Android Studio的安装目录下bin\\lldb\\bin的minidump_stackwalk。命令如下：  
~~~
minidump_stackwalk crashDump/***.dmp >crash.txt 
~~~

成功解析文件。大家看下

~~~rust
Operating system: Android
                  0.0.0 Linux 3.10.0+ #256 SMP PREEMPT Fri May 19 11:58:12 PDT 2017 i686
CPU: x86
     GenuineIntel family 6 model 31 stepping 1
     4 CPUs

GPU: UNKNOWN

Crash reason:  SIGSEGV
Crash address: 0x0
Process uptime: not available

Thread 0 (crashed)
 0  libcrash-lib.so + 0x544
  好，从.dump文件解析出来的信息中，根据文章[Android 平台 Native 代码的崩溃捕获机制及实现](https://mp.weixin.qq.com/s/g-WzYF3wWAljok1XjPoo7w?)的介绍，我们可知Crash reason: SIGSEGV代表哪种类型的错误：

SIGSEGV 是当一个进程执行了一个无效的内存引用，或发生段错误时发送给它的信号。
Thread 0 (crashed) // crash 发生时候的线程
 0  libcrash-lib.so + 0x544 // 发生 crash 的位置和寄存器信息 
~~~

4、有了具体的寄存器信息，我们进行符号解析，可以使用Android NDK(低版本14)中提供的addr2line来根据地址进行一个符号反解的过程，该工具在Android SDK目录下可以找到。  
  工具链的选择要根据.so的类型来决定，看解析后的文件，有显示CPU信息。下面是NDK 18的工具链目录：  
      $NDK\_HOME/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86\_64/bin/arm-linux-androideabi-addr2line

     如果是arm-64位的so，解析是需要使用aarch64-linux-android-4.9下的工具链。  
  如果是arm-32位的so，解析是需要使用arm-linux-androideabi-4.9下的工具链。  
  如果是x86-64位的so，解析是需要使用x86\_64-4.9下的工具链。  
  如果是x86-32位的so，解析是需要使用x86-4.9下的工具链。  
  这里，因为CPU信息是x86，所以选择x86-4.9下的工具链。我们将项目中build目录下的x86对应的libcrash-lib.so(app\\build\\intermediates\\merged_native_libs\\debug\\out\\lib\\arm64-v8a\\libcrash-lib.so)拷贝到x86-4.9下的工具链目录(x86-4.9\\prebuilt\\windows-x86\_64\\bin)中，然后执行如下命令：  
~~~
arm-linux-androideabi-addr2line -f -C -e sample/build/intermediates/transforms/mergeJniLibs/debug/0/lib/armeabi-v7a/libcrash-lib.so 0x554 
~~~

  
  是不是看到具体哪个类下的哪一行报错了！！！

