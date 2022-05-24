package com.yun.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.helpers.ThreadLocalMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: yunCrush
 * Date:2022/5/10 10:10
 * Description: 关于ThreadLocal的注意事项及使用,
 */
@Slf4j
public class ThreadLocalTest {
    static SimpleDateFormat sdf = new SimpleDateFormat("yy-mm-dd HH:mm:ss");

    public static Date parse(String dateStr) throws ParseException {
        return sdf.parse(dateStr);
    }

    // 使用Threadlocal优化
    public static final ThreadLocal<SimpleDateFormat> sdfThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yy-mm-dd HH:mm:ss"));

    public static final Date parseDateByThreadLocal(String dateStr) throws ParseException {
        return sdfThreadLocal.get().parse(dateStr);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    // System.out.println(ThreadLocalTest.parse("2022-05-25 12:30:00"));
                    System.out.println(ThreadLocalTest.parseDateByThreadLocal("2022-05-25 12:30:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    // 避免内存泄露
                    sdfThreadLocal.remove();
                }
            }, String.valueOf(i)).start();
        }
    }
}
