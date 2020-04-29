package com.bolt.threadlocal;

import com.bolt.util.CountDownLatchUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/27
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class FastThreadLocalTest {
    private static final Executor executor = Executors.newFixedThreadPool(3);
    private static final ThreadLocal<Person> threadLocal = new ThreadLocal<Person>() {
        @Override
        protected Person initialValue() {
            return new Person();
        }
    };
    private static final FastThreadLocal<Integer> fastThreadLocal1 = new FastThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() throws Exception {
            return 100;
        }

        @Override
        protected void onRemoval(Integer value) throws Exception {
            System.out.println(value + ":我被删除了");
        }
    };

    private static final FastThreadLocal<Person> fastThreadLocal2 = new FastThreadLocal<Person>() {
        @Override
        protected Person initialValue() throws Exception {
            return new Person();
        }
    };

    @Test
    public void testSetAndGetByCommonThread() {
        Integer x = fastThreadLocal1.get();
        System.out.println(x);
        fastThreadLocal1.set(200);
    }

    @Test
    public void testSetAndGetByFastThreadLocalThread() {
        new FastThreadLocalThread(() -> {
            Integer x = fastThreadLocal1.get();
            fastThreadLocal1.set(200);
        }).start();
    }


    @Test
    public void testThreadLocal() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        CountDownLatch latchmain = new CountDownLatch(1);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    latchmain.await();
                    System.out.println(Thread.currentThread().getName() + " : " + fastThreadLocal2.get());
                    latch.countDown();
                } catch (Exception e) {
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(FastThreadLocalRunnable.wrap(runnable));
        }
        latchmain.countDown();
        latch.await();
    }

    static class Person {
    }
}
