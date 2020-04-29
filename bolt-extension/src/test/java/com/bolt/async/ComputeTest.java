package com.bolt.async;

import com.bolt.util.CountDownLatchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/8
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class ComputeTest {
    private ConcurrentHashMap<String, FutureTask<Connection>> connectionPool = new ConcurrentHashMap<String, FutureTask<Connection>>();

    @Test
    public void test1() {
        List<ComputeFutureTask<Integer>> taskList = new ArrayList<ComputeFutureTask<Integer>>();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            ComputeFutureTask<Integer> ft = new ComputeFutureTask(new ComputeCallable(i, "task-" + i));
            taskList.add(ft);
            executor.execute(ft);
        }

        Integer res = 0;
        for (ComputeFutureTask<Integer> task : taskList) {
            try {
                res += task.get();
            } catch (Exception e) {
            }
        }
        System.out.println(res);
        executor.shutdown();
    }

    /**
     * 同一个任务的call多线程只会被执行一次
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        ComputeFutureTask<Integer> ft = new ComputeFutureTask(new ComputeCallable(100, "task-" + 100));
        CountDownLatchUtil latchUtil = new CountDownLatchUtil();
        latchUtil.latch(() -> {
            ft.run();
        });
    }

    @Test
    public void getOrCreateConnectionTest() throws Exception {
        CountDownLatchUtil latchUtil = new CountDownLatchUtil();
        latchUtil.latch(() -> {
            try {
                Connection c = getOrCreateConnection("127.0.0.1:8081");
                System.out.println(c);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public Connection getOrCreateConnection(String key) throws Exception {
        // ConcurrentHashMap get方法无锁并发
        FutureTask<Connection> connectionFutureTask = connectionPool.get(key);
        if (connectionFutureTask == null) {
            Callable callable = new Callable<Connection>() {
                @Override
                public Connection call() throws Exception {
                    return createConnection();
                }
            };

            System.out.println("Callable: " + callable);

            ComputeFutureTask<Connection> newTask = new ComputeFutureTask<Connection>(callable);
            System.out.println("NewTask: " + newTask);

            // cas无锁操作
            connectionFutureTask = connectionPool.putIfAbsent(key, newTask);
            if (connectionFutureTask == null) {
                connectionFutureTask = newTask;
                connectionFutureTask.run();
                System.out.println("Join Pool Task : " + connectionFutureTask);
            }
        }
        return connectionFutureTask.get();
    }

    private class Connection {

    }

    private Connection createConnection() {
        return new Connection();
    }

}
