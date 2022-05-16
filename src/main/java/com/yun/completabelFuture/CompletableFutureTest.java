package com.yun.completabelFuture;

import java.util.concurrent.*;

/**
 * Author: yunCrush
 * Date:2022/4/4 0:08
 * Description: 异步函数式编程
 * 其他的子任务处理完成后：主动通知给Main
 */
public class CompletableFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1开始处理逻辑");
            try {
                m1();
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
            // 异步编排，方法众多
        }).thenApply(f -> {
            return f + 2;
        }).whenComplete((v, e) -> {
            // e: exception
            if (e == null) {
                System.out.println("result : " + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        try { TimeUnit.SECONDS.sleep(5); } catch (Exception e) { e.printStackTrace();}

        System.out.println("Main 线程结束"+Thread.currentThread().getName());

        // System.out.println(CompletableFuture.supplyAsync(() -> "abc").thenApply(r -> r + "123").join());
    }

    private static void m1() throws ExecutionException, InterruptedException {
        // 定义一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
                20,
                20L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(50),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        // m1(threadPoolExecutor);
        // threadPoolExecutor.shutdown();
        // 无传入值与返回值
        CompletableFuture<Void> comFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println("处理逻辑1" + Thread.currentThread().getName());
        });
        System.out.println("1: " + comFuture1.get());
        // 同时带线程池
        CompletableFuture<Void> comFuture2 = CompletableFuture.runAsync(() -> {
            System.out.println("处理逻辑2" + Thread.currentThread().getName());
        }, threadPoolExecutor);
        System.out.println("2: " + comFuture1.get());
        // 无传入值，带返回值,默认线程池
        CompletableFuture<Integer> comFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("处理逻辑3" + Thread.currentThread().getName());
            return 3;
        });
        System.out.println("3:" + comFuture3.get());
        // 带线程池
        CompletableFuture<Integer> comFuture4 = CompletableFuture.supplyAsync(() -> {
            System.out.println("处理逻辑4" + Thread.currentThread().getName());
            return 4;
        }, threadPoolExecutor);
        System.out.println("4: " + comFuture4.get());
    }
}
