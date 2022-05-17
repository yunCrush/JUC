package com.yun.atomics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Author: yunCrush
 * Date:2022/5/17 19:08
 * Description:
 * 对象的属性修改原子类
 * ·	AtomicIntegerFieldUpdater
 * <p>
 * ·	原子更新对象中int类型字段的值
 * ·	AtomicLongFieldUpdater
 * <p>
 * ·	原子更新对象中Long类型字段的值
 * ·
 */
class BankAccount {
    private String bankName = "CCB";//银行
    public volatile int money = 0;//钱数
    AtomicIntegerFieldUpdater<BankAccount> accountAtomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

    //不加锁+性能高，局部微创
    public void transferMoney(BankAccount bankAccount) {
        accountAtomicIntegerFieldUpdater.incrementAndGet(bankAccount);
    }
}
/**
 * * 以一种线程安全的方式操作非线程安全对象的某些字段。
 * * 需求：
 * * 1000个人同时向一个账号转账一元钱，那么累计应该增加1000元，
 * * 除了synchronized和CAS,还可以使用AtomicIntegerFieldUpdater来实现
 */

/**
 * * AtomicReferenceFieldUpdater
 *  * *
 *  * * ·	原子更新引用类型字段的值
 *  * * ·	使用目的
 *  * * ·	以一种线程安全的方式操作非线程安全对象内的某些字段
 *  * * ·	使用要求
 *  * * ·	更新的对象属性必须使用 public volatile 修饰符。
 *  * * ·	因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须 使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属
 */

public class AtomicIntegerFieldUpdaterDemo {
    public static void main(String[] args) {
        MyVar myVar = new MyVar();
        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                myVar.init(myVar);
            }, String.valueOf(i)).start();
        }
    }

    private static void m1() {
        BankAccount bankAccount = new BankAccount();
        for (int i = 1; i <= 1000; i++) {
            int finalI = i;
            new Thread(() -> {
                bankAccount.transferMoney(bankAccount);
            }, String.valueOf(i)).start();
        }
        //暂停毫秒
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(bankAccount.money);
    }
}


/**
 *  * 多线程并发调用一个类的初始化方法，如果未被初始化过，将执行初始化工作，要求只能初始化一次
 */
class MyVar {
    public volatile Boolean isInit = Boolean.FALSE;
    AtomicReferenceFieldUpdater<MyVar, Boolean> atomicReferenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");


    public void init(MyVar myVar) {
        if (atomicReferenceFieldUpdater.compareAndSet(myVar, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(Thread.currentThread().getName() + "\t" + "---init.....");
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "\t" + "---init.....over");
        } else {
            System.out.println(Thread.currentThread().getName() + "\t" + "------其它线程正在初始化");
        }
    }
}

