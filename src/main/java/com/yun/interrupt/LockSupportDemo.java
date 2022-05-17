package com.yun.interrupt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther yunCrush
 */
public class LockSupportDemo {
    static Object objectLock = new Object();//持有锁，方便抢占资源
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args)//main方法一切程序的入口,main主线程
    {
        // Thread a = new Thread(() -> {
        //     System.out.println(Thread.currentThread().getName() + "\t" + " come in");
        //     LockSupport.park();
        //     System.out.println(Thread.currentThread().getName() + "\t" + " 被唤醒");
        // }, "a");
        // a.start();
        //
        // new Thread(() -> {
        //     LockSupport.unpark(a);
        //     System.out.println(Thread.currentThread().getName()+"\t"+" 发出通知");
        // },"b").start();
        //  如果需要阻塞两次
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + " come in");
            LockSupport.park();
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + "\t" + " 被唤醒");
        }, "t1");
        t1.start();

        Thread t2 = new Thread( () -> {
                LockSupport.unpark(t1);
                System.out.println(Thread.currentThread().getName()+"\t"+" 发出通知");
            },"t2");
        t2.start();

        Thread t3 = new Thread( () -> {
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName()+"\t"+" 发出通知");
        },"t3");
        t3.start();
}

    public static void lockAwaitSignal() {
        new Thread(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t" + " come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t" + " 被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "a").start();
        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t" + " 发出通知");
            } finally {
                lock.unlock();
            }
        }, "b").start();
    }

    public static void syncWaitNotify() {
        new Thread(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t" + "come in");
                try {
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t" + "被唤醒");
            }
        }, "a").start();
        new Thread(() -> {
            synchronized (objectLock) {
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t" + "发出通知");
            }
        }, "b").start();
    }
}