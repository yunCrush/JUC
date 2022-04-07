package com.yun;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Author: yunCrush
 * Date:2022/4/4 11:22
 * Description:
 */
public class CompletableNetMallTest {
    static List<Mall> list = Arrays.asList(
            new Mall("jd"),
            new Mall("pdd"),
            new Mall("tb")
    );

    public static List<String> getPriceByStep(List<Mall> list, String productName) {
        return list
                .stream()
                .map(mall -> String.format(productName + "  in %s is %.2f", mall.getMallName(), mall.calPrice(productName))).collect(Collectors.toList());
    }

    // 异步编程，多箭齐发
    public static List<String> getPriceByAsyn(List<Mall> list, String productName) {
        return list
                .stream()
                .map(mall -> CompletableFuture.supplyAsync(() -> String.format(productName + "  in %s is %.2f", mall.getMallName(), mall.calPrice(productName))))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<String> list1 = getPriceByStep(list, "mysql");
        for (String element : list1) {
            System.out.println(element);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime - startTime) + " 毫秒");
        System.out.println();
        long startTime2 = System.currentTimeMillis();
        List<String> list2 = getPriceByAsyn(list, "mysql");
        for (String element : list2) {
            System.out.println(element);
        }
        long endTime2 = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime2 - startTime2) + " 毫秒");
    }
}
 class Mall {
    @Getter
    String mallName;

    public Mall(String mallName) {
        this.mallName = mallName;
    }

    public double calPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2;
    }
}