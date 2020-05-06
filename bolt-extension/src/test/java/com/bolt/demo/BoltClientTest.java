package com.bolt.demo;

import com.bolt.config.BoltClientOption;
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
        bootstrap.option(BoltClientOption.CONNECT_TIMEOUT, 3000)
                .option(BoltClientOption.HEARTBEATINTERVAL, 5000);
        bootstrap.startUp();
        ReqBody requestBody = new ReqBody();
        requestBody.setName("zhang");
        requestBody.setAge(20);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Url.CONNECT_TIMEOUT_KEY, 9000);
//        map.put(Url.ASYNC, true);
        Url url = Url.builder()
                .host("127.0.0.1")
                .port(9091)
                .setParameters(map)
                .build();
        try {
            String body = bootstrap.request(url, requestBody);
            System.out.println("Client recv: " + body);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        new CountDownLatch(1).await();

    }
}
