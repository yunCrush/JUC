package com.yun.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 */
public class ABADemo
{
    static AtomicInteger atomicInteger = new AtomicInteger(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100,1);
    static AtomicMarkableReference<Integer> markableReference = new AtomicMarkableReference<>(100,false);
    public static void main(String[] args)
    {
        System.out.println("============AtomicMarkableReference不关心引用变量更改过几次，只关心是否更改过======================");

        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName()+"\t 1次版本号"+marked);
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            markableReference.compareAndSet(100,101,marked,!marked);
            System.out.println(Thread.currentThread().getName()+"\t 2次版本号"+markableReference.isMarked());
            markableReference.compareAndSet(101,100,markableReference.isMarked(),!markableReference.isMarked());
            System.out.println(Thread.currentThread().getName()+"\t 3次版本号"+markableReference.isMarked());
        },"t5").start();

        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName()+"\t 1次版本号"+marked);
            //暂停几秒钟线程
            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
            markableReference.compareAndSet(100,2020,marked,!marked);
            System.out.println(Thread.currentThread().getName()+"\t"+markableReference.getReference()+"\t"+markableReference.isMarked());
        },"t6").start();


    }

    private static void m1() {
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName()+"\t"+"---默认版本号: "+stamp);
            //让后面的t4获得和t3一样的版本号，都是1，好比较
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

            atomicStampedReference.compareAndSet(100,101,stamp,stamp+1);
            System.out.println(Thread.currentThread().getName()+"\t"+"---修改1次版本号: "+atomicStampedReference.getStamp());
            atomicStampedReference.compareAndSet(101,100,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"\t"+"---修改2次版本号: "+atomicStampedReference.getStamp());
        },"t3").start();
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName()+"\t"+"---默认版本号: "+stamp);
            //上前面的t3完成ABA问题
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            boolean result = atomicStampedReference.compareAndSet(100, 20210308, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName()+"\t"+"---操作成功否："+result+"\t"+atomicStampedReference.getStamp()+"\t"+atomicStampedReference.getReference());
        },"t4").start();
    }

    public static void abaProblem()
    {
        new Thread(() -> {
            atomicInteger.compareAndSet(100,101);
            atomicInteger.compareAndSet(101,100);
        },"t1").start();

        //暂停毫秒
        try { TimeUnit.MILLISECONDS.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

        new Thread(() -> {
            boolean b = atomicInteger.compareAndSet(100, 20210308);
            System.out.println(Thread.currentThread().getName()+"\t"+"修改成功否："+b+"\t"+atomicInteger.get());
        },"t2").start();
    }
}
