package com.yun.threadlocal;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

/**
 * Author: yunCrush
 * Date:2022/5/24 22:57
 * Description: threadlocal 强软弱虚引用实践
 */
class MyObject {

    //一般开发中不用调用这个方法，本次只是为了讲课演示
    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread().getName() + "\t" + "---finalize method invoked....");
    }
}

public class ReferenceDemo {
    public static void main(String[] args) {
        //当我们内存不够用的时候，soft会被回收的情况，设置我们的内存大小：-Xms10m -Xmx10m
        SoftReference<MyObject> softReference = new SoftReference<>(new MyObject());
        System.gc();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----gc after内存够用: " + softReference.get());
        try {
            byte[] bytes = new byte[9 * 1024 * 1024];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("-----gc after内存不够: " + softReference.get());
        }
    }
    public static void strongReference()
    {
        MyObject myObject = new MyObject();
        System.out.println("-----gc before: "+myObject);

        myObject = null;
        System.gc();
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("-----gc after: "+myObject);
    }
}
