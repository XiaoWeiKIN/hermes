package com.bolt.timertask;

import com.bolt.common.Url;
import com.bolt.util.NamedThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangxw
 * @DateTime: 2020/5/2
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class TimerTaskTest {
    private static final HashedWheelTimer IDLE_CHECK_TIMER = new HashedWheelTimer(
            new NamedThreadFactory("dubbo-client-idleCheck", true), 1, TimeUnit.SECONDS, 128);
    LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();

    @Test
    public void test() throws Exception {
        IDLE_CHECK_TIMER.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                while (true) {
                    String str = queue.take();
                    System.out.println(str);
                }
            }
        }, 0, TimeUnit.SECONDS);

        queue.put("张三");
        new CountDownLatch(1).await();
    }

    public void ss() throws IllegalArgumentException {
        throw new IllegalArgumentException("sss");
    }
    @Test
    public void reconnectTimerTaskTest() throws InterruptedException {
//        ReconnectTimerTask task = new ReconnectTimerTask();
//        IDLE_CHECK_TIMER.newTimeout(task, 0, TimeUnit.SECONDS);
//        System.out.println(task);
//        System.out.println(IDLE_CHECK_TIMER);
//        TimeUnit.SECONDS.sleep(2);
//
//        task.reconnect(new Url("123",90));
//        new CountDownLatch(1).await();
    }


}
