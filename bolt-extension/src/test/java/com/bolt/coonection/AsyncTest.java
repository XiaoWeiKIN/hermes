package com.bolt.coonection;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/15
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class AsyncTest {

    @Test
    public void promiseTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventExecutor executorA = new DefaultEventExecutor(new DefaultThreadFactory("EventA"));
        EventExecutor executorB = new DefaultEventExecutor();
        Channel channel = new NioSocketChannel();
        // 为EventLoop注册一个Promise
        Promise<Channel> newPromise = executorA.<Channel>newPromise();
        System.out.println(Thread.currentThread().getName());
        newPromise.addListener(f -> {
            if (f.isSuccess()) {
                Assert.assertEquals(channel, f.getNow());
                System.out.println(Thread.currentThread().getName());
                latch.countDown();
            }
        });
        Assert.assertEquals(false, executorB.inEventLoop());
        executorB.execute(new Runnable() {
            @Override
            public void run() {
                newPromise.setSuccess(channel);
            }
        });
        latch.await();
    }
    @Test
    public void succeededFutureTest() {
        EventExecutor executor = new DefaultEventExecutor();
        Future<Boolean> future = executor.newSucceededFuture(Boolean.TRUE);
        if (future.isDone()) {
            Assert.assertEquals(true, future.getNow());
        }
    }

    @Test
    public void scheduleTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EventExecutor executor = new DefaultEventExecutor();
        ScheduledFuture<?> schedule = executor.schedule(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 5, TimeUnit.SECONDS);

        schedule.cancel(false);

        Assert.assertEquals(true,schedule.isDone());
        Assert.assertEquals(true,schedule.isCancelled());

        latch.await();
    }

}
