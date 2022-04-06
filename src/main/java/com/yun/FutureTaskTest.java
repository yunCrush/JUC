package com.yun;


import java.util.concurrent.*;

/**
 * Author: yunCrush
 * Date:2022/4/3 23:32
 * Description: FutureTask fork一个子线程完成相关逻辑，主线程正常运行，最后获取子线程的结果
 * 缺点：future耗时，阻塞，规范，放在最后，使用超时机制
 * 使用轮询代替阻塞
 * 多线程异步编排：多个futureTask 进行编排，一定顺序排列 买鱼->调料->水煮鱼
 * 3个步骤同时做，做完后直接组合 CompletableFuture
 */
public class FutureTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<Integer> futureTask = new FutureTask(() -> {
            System.out.println("子线程逻辑处理中");
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 返回一个值，代表子线程处理结果
            return 1024;
        });
        new Thread(futureTask, "t1").start();
        // 2. 过时不候 超时机制等待3秒，拿不到结果返回
        // futureTask.get(3, TimeUnit.MILLISECONDS);
        // 3. 轮询 使用轮询替代，也不建议
        while (true) {
            if (futureTask.isDone()) {
                System.out.println("拿到处理结果：-->"+futureTask.get());
                break;
            }else {
                System.out.println("还在处理逻辑中，别催");
            }
        }
        System.out.println("main 线程处理逻辑");
        // futureTask,get(timeout,TimeUnit) 超时机制
        //1.不见不散 System.out.println(futureTask.get());

    }
}
