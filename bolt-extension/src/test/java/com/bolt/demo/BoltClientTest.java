package com.bolt.demo;

import com.bolt.config.BoltClientOption;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.Url;
import com.bolt.config.BoltRemotingOption;
import com.bolt.protocol.ReqBody;
import com.bolt.transport.BoltClient;
import com.bolt.util.CountDownLatchUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/10
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class BoltClientTest {
    private static final Logger logger = LoggerFactory.getLogger(BoltClientTest.class);
    BoltClient client;

    @Before
    public void setUp() {
        client = new BoltClient();
        client.option(BoltClientOption.CONNECT_TIMEOUT, 3000);
        client.startUp();
    }

    @Test
    public void sync_test() {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        String body = client.request(url, requestBody);
        logger.info("Client Recv : " + body);
    }


    @Test
    public void async_test() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 异步调用
        map.put(Url.ASYNC, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future = client.request(url, requestBody);
        logger.info("Client Recv : " + future.get());
    }

    @Test
    public void call_back() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 异步调用
        map.put(Url.ASYNC, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        CompletableFuture<String> future = client.request(url, requestBody);
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((res, cause) -> {
            if (cause != null) {
                // 异常处理
            }
            latch.countDown();
            logger.info("Client Recv : " + res);
        });
        latch.await();
    }

    @Test
    public void oneway_test() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 设置连接超时时间
        map.put(Url.CONNECT_TIMEOUT, 9000);
        // 单向调用
        map.put(Url.ONEWAY, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        client.request(url, requestBody);
    }


}
