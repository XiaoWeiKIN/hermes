package com.bolt.common;


import com.bolt.util.NetUtils;
import com.bolt.util.UrlUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/7
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class UrlTest {

    @Test
    public void test_equals() {
        InetSocketAddress socketAddress1 = new InetSocketAddress("127.0.0.1", 80);
        InetSocketAddress socketAddress2 = new InetSocketAddress("127.0.0.1", 80);
        Assert.assertEquals(socketAddress1, socketAddress2);
    }

    @Test
    public void test_url_equals() {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Url.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION);
        Url urlA = Url.builder().port(80).host("123.0.0.1")
                .setParameters(option).build();

        Url urlB = Url.builder()
                .port(80).host("123.0.0.1")
                .setParameters(option).build();
        option = new HashMap<String, Object>();
        option.put(Url.MAX_CONNECTION, 10);

        Url urlC = Url.builder()
                .port(80).host("123.0.0.1")
                .setParameters(option).build();
        Assert.assertEquals(urlA, urlB);
        Assert.assertNotEquals(urlA, urlC);
    }

    @Test
    public void test_url_add() {
        Map<String, Object> option = new HashMap<String, Object>();
        option.put(Url.SERIALIZATION_KEY, "hessian2");
        option.put(Url.MAX_CONNECTION, 10);

        Url url = Url.builder()
                .port(80).host("123.0.0.1")
                .setParameters(option).build();
        option = new HashMap<String, Object>();
        option.put(Url.MAX_CONNECTION, 20);
        url.addParameters(option);
        System.out.println(url.toString());
    }


    @Test
    public void test_url_parse() {

    }
}