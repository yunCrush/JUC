# JUC
1. CompletableFuture：继承Future,扩展，思想可完全替换FutureTask,FutureTask.get()阻塞，
join == get，join不会抛出异常
    ```$xslt
    public class CompletableFuture<T> implements Future<T>, CompletionStage<T>
    ```
2. 主线程不要立刻结束，否则CompletableFuture使用的线程池会立刻关闭
3. CompletableFuture API使用：
    ```$xslt
    a.获得结果和触发计算
    get(),get(long , TimeUnit ),getNow(defaultValue)不会阻塞，立即获取值，计算完毕则返回，否则返回默认值
    boolean complete(defaultValue),执行complete时，结果计算是否完毕。打断成功，返回future.get(),失败则给默认值
    ```

    ```$xslt
    b.对结算结果进行处理 串行化，中间报错就停止：thenApply(Function)-->whenComplete((v,e)->{})-->exceptionally(e->{}).join
    handle((function,e)->{}) 带着异常顺步执行
    总结：exceptionally ===> try catch, whenComplete+handle ==>try finally,
    handleAsync whenCompleteAsync 交给线程池其他线程处理，不带后缀就是一个线程执行
    ```
    ```$xslt
    c.接收任务的处理结果，并进行消费，无返回值 thenAccpet(T t)消费无返回
   任务之间的执行顺序：thenRun(Runnable),thenApply((v)->{return m},thenAccept((v)->{})
   总结：thenRun(Runnable) 任务Ａ执行完后执行Ｂ，但是Ｂ不需要Ａ结果
         thenApply(Function) B需要A结果，同时有返回
         thenAccept(Consumer) B需要A结果，无返回值
    ```
   ```$xslt
   d.对计算速度进行选用 applyToEither()
   两个Step，谁先结束，返回谁的结果
    ```
   ```$xslt
    e. 对计算结果进行合并 thenCombine(),研究thenCompose()
    多个completionStage 先完成的等待后完成的，完成后一起进行结果的合并
    thenCombine(completionFuture,(r1,r2)->{})
    ```
4. 锁
    ```$xslt
    synchronized与reentrantlock 悲观锁  cas 乐观锁
    ```
    - 8种锁现象的演示
    ```$xslt
    类Phone 普通sendEmail() sendSMS()  控制email先与sms启动谁先 
    1. 标准访问 synchronied 修饰普通方法
    2. email 里面 TimeUnit.SECONDS.sleep(3)   => sleep不释放锁
    3. 新增一个普通hello(), email or hello   => hello() 不需要锁
    4. 2部Phone, phone1.sendEmail,phone2.sendSMS()  => sms synchronized锁当前对象
    5. 两个静态同步方法，同一个手机   => static修饰 锁类对象 email
    6. 两个静态同步方法，2个手机   => static修饰 锁类对象 email
    7. 1个静态同步方法，1个普通同步方法，1个手机   =>  sms
    8. 1个静态同步方法，1个普通同步方法，2个手机   =>  sms 
    ```
    - 8种现象总结
    ```$xslt
    synchronized修饰普通成员方法锁的是this对象，不同对象时，不影响。一个对象时只有一个线程可访问this的syn方法，hello不需要锁
    修饰static方法  锁的是当前类 
    类锁与对象锁互相独立，7-8问题
    修饰代码块，修饰的是括号里配置的对象的锁
    ```
   - synchronized同步代码块 syn(obj)
   ```$xslt
    monitorenter与monitorexit 并非1:1， 多出的monitorexit保证出异常也可正常释放锁
    如果直接在代码块内部抛出异常，出现athrow 1对，enter与exit配对 
    ```
    - synchronized 普通同步方法 syn method(){}
    ```$xslt
       flag： ACC_SYNCHRONIZED 
    ```
    - synchronized static 同步方法 static syn method(){}
    ```$xslt
       flag： ACC_STATIC  ACC_SYNCHRONIZED
       ObjectMonitor.java -> ObjectMonitor.cpp -> ObjectMonitor.hpp
       锁升级主要依赖于MarkWord的锁标志位和释放偏向锁标志位
    ```
    - 公平锁与非公平锁
    ```$xslt
        ReentrantLock 默认非公平锁,Syn是非公平锁
        公平与非公平是判断同步队列中是否有先驱节点，刚释放锁的线程再次获得锁的概率非常大，
        使用非公平的优点是减少线程开销，提高性能，会出现“锁饥饿”
    ```
    - 可重入锁与不可重入锁
    ```$xslt
     syn与reentrantlock都是可重入锁，syn默认可重入锁
     在syn修饰的代码块或者方法中，调用本类的其他同步方法或代码块，可直接进入，是可以直接获取到锁的
     每个对象拥有一个锁计数器和一个指向 持有该锁线程的指针
    ```
5. 死锁
    - 死锁排除
    ```$xslt
    jps+jstack: jps -l  jstack+pid 
    jconsole
    ```
6. 线程中断机制
    - void interrupt() 中断协商机制
    ```$xslt
    手动调用该线程的interrupt()仅仅将中断标识设置为true，线程正常运行，并不能立即中断
   底层调用interrupt0(),join() wait() sleep()被中断抛中断异常
    ```
   **线程立即退出被阻塞状态，中断异常会把标志位复位为false,需要再调用一次Thread.currentThread().interrupt()**
    - static boolean interrupted()
    ```$xslt
    Thread.interrupted() 返回当前的线程中断状态，重置标识位
    ````
    -  boolean isInterrupted()
    ```$xslt
    返回中断状态
    ```
   **总结**: interrupted与IsInterrupted()都调用的是isInterrupted(ClearInterrupted),
   前者传入true，需要重置标志位为false
   - 优雅中断线程
   ```$xslt
    1. static volatile boolean isStop = false 修改变量值，进行中断
    2. static AtomicBoolean a = new AtomicBoolean(false);
        a.set(true);
        if(a.get()){} 
    ```
   
7. LockSupport 线程唤醒与等待
    - Object wait() notify()  (syn+wait+notify)
    - Condition await() signal() (lock+await+signal)
        ReentrantLock lock = new ReentrantLock();
        Condition con = lock.newCondition(); 
    - LockSupport
        是用来创建锁和其他同步类的基本线程阻塞原语，park()阻塞线程 unpark(thread) 解除阻塞
        park()将permit设置为0，进行阻塞，unpark()将permit设置为1,不可累加只有1，自动唤醒被阻塞的线程
        
    **总结**: LockSupport无需锁块，且不用遵守先阻塞再唤醒。wait notify和await signal需遵守，unpark只管一次。 
8. 内存屏障
    - JSR-133定义happens-before规则：6点，是一种规范
    
    要求编译器在生成jvm指令时，插入特定的内存屏障指令来保证指令不会重排序以及有序性。volatile不会保证原子性
    - 4条cpu的屏障指令
    ```$xslt
    loadload() storestore() loadstore() storeload()
    一读，二写，写+读  (写前后，读后)。写前：storestore 写后storeload 读后loadstore loadload
    (解释：第一个是volatile读时，不可重排序，保证volatile读后的操作不会被重排序到前面去)
    (第二个是volatile写时，不可重排序，保证volatile写前的操作不会被重排序到后面去)
    (第一个是volatile写时，第二个是volatile读时，不可重排序)
    volatile写之前插入storestore屏障，之后插入storeload屏障
    volatile读后面插入一个loadload屏障，之后插入一个loadstore屏障
    ```
    - volatile读写过程
    ```$xslt
    read ->load -> use(使用) -> assign(赋值) -> store -> write -> lock -> unlock  
    ```
   ![](https://cdn.nlark.com/yuque/0/2022/png/361120/1649325239990-8b1c738c-8c95-48b7-9aa5-369f1ef9d82c.png?x-oss-process=image%2Fresize%2Cw_900%2Climit_0)
    - volatile复合操作(i++)不具备原子性
    不具备原子性的原因在于use-> assign阶段 方法需要加syn锁
    所以volatile修饰的变量，只适合用来保存某个状态的Boolean值或者int值，不做复杂操作