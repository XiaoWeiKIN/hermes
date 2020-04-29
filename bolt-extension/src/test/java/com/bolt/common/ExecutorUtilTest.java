package com.bolt.common;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class ExecutorUtilTest {
    ExecutorService executor = Executors.newFixedThreadPool(5);

    @Before
    public void setUp() {

    }

    @Test
    public void shutDownNow_test() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println("task1 has completion");

                } catch (InterruptedException e) {
                    System.out.println("task1 has Interrupted");
                }

            }
        });

        for (int i = 0; i < 5; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println("task2 has completion");

                    } catch (InterruptedException e) {
                        System.out.println("task2 has Interrupted");
                    }

                }
            };
            System.out.println("runnable "+runnable);
            executor.execute(runnable);
        }

        List<Runnable> runnables = executor.shutdownNow();
        runnables.forEach(System.out::println);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("....");
            }
        });
//        try {
//            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
//                System.out.println("awaitTermination : ");
//                executor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            System.out.println("awaitTermination interrupted: " + e);
//            executor.shutdownNow();
//        }


        System.out.println(executor.isTerminated());
    }
}
