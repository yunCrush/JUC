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
    - 总结
    ```$xslt
    synchronized修饰普通成员方法锁的是this对象，不同对象时，不影响。一个对象时只有一个线程可访问this的syn方法，hello不需要锁
    修饰static方法  锁的是当前类 
    类锁与对象锁互相独立，7-8问题
    修饰代码块，修饰的是括号里配置的对象的锁
    ``` 
5. 这是第五点

  