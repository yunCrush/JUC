package com.yun.completabelFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Author: yunCrush
 * Date:2022/5/16 13:55
 * Description:
 */
public class CompletableFutureDemo3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {
            return f + 2;
        }).thenApply(f -> {
            return f + 3;
        }).thenApply(f -> {
            return f + 4;
            // 接受参数消费无结果
        }).thenAccept(r -> System.out.println(r));
        // thenRun A 执行完后执行B， B不需要A结果
        // thenAccept A 执行完后执行B， B需要A结果，无返回值
        // thenApply A 执行完后执行B， B需要A结果，且有返回值
    }
}
