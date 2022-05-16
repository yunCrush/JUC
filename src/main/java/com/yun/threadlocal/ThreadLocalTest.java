package com.yun.threadlocal;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: yunCrush
 * Date:2022/5/10 10:10
 * Description:
 */
@Slf4j
public class ThreadLocalTest {
    public static ExecutorService threadLocal = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        for ( int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("名称: {}, 号码：{}");

                }
            });
        }
    }
}
