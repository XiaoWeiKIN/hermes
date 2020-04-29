package com.bolt.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchUtil {
    private CountDownLatch start;
    private CountDownLatch end;
    private int poolSize;

    public CountDownLatchUtil() {
        this(10);
    }

    public CountDownLatchUtil(int poolSize) {
        this.poolSize = poolSize;
        start = new CountDownLatch(1);
        end = new CountDownLatch(poolSize);
    }

    public void latch(MyFunctionalInterface functionalInterface) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        start.await();
                        functionalInterface.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        end.countDown();
                    }
                }
            };
            service.execute(runnable);
        }

        start.countDown();
        end.await();
    }

    @FunctionalInterface
    public interface MyFunctionalInterface {
        void run();
    }
}