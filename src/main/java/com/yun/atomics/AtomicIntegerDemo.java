package com.yun.atomics;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class MyNumber
{
    @Getter
    AtomicInteger atomicInteger = new AtomicInteger();

    public Integer addPlusPlus()
    {
        return atomicInteger.incrementAndGet();
    }
}

/**
 */
public class AtomicIntegerDemo
{
    public static void main(String[] args) throws InterruptedException
    {
        MyNumber myNumber = new MyNumber();
        CountDownLatch countDownLatch = new CountDownLatch(50);


        for (int i = 1; i <=50; i++) {
            new Thread(() -> {
                try
                {
                    for (int j = 1; j <=1000; j++) {
                        myNumber.addPlusPlus();
                    }
                }finally {
                    countDownLatch.countDown();
                }
            },String.valueOf(i)).start();
        }

        countDownLatch.await();

        System.out.println(Thread.currentThread().getName()+"\t"+"---result: "+myNumber.getAtomicInteger().get());


    }
}

