package com.bolt.demo;

import com.bolt.reomoting.RemotingContext;
import com.bolt.common.Url;
import com.bolt.config.BoltRemotingOption;
import com.bolt.protocol.ReqBody;
import com.bolt.transport.BoltClient;
import com.bolt.util.CountDownLatchUtil;
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
public class BoltClientTest {
    private static final Logger logger = LoggerFactory.getLogger(BoltClientTest.class);

    public static void main(String[] args) throws Exception {
        CountDownLatchUtil latch = new CountDownLatchUtil(20);
        BoltClient bootstrap = new BoltClient();
        bootstrap.option(BoltRemotingOption.CONNECT_TIMEOUT, 3000);
        bootstrap.startUp();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Url.CONNECT_TIMEOUT_KEY, 9000);
        map.put(Url.ASYNC_KEY, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        bootstrap.request(url, requestBody);
        CompletableFuture<String> future = RemotingContext.getContext().getCompletableFuture();

        latch.latch(() -> {
//        future.whenComplete((v,e)->{
//            downLatch.countDown();
//            logger.info(v);
//        });
            try {
                String s = future.get();
                logger.info(s);
            } catch (Exception e) {

            }

        });

//        downLatch.await();

    }
}
