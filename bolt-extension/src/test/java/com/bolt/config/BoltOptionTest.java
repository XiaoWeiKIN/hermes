package com.bolt.config;

import com.bolt.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/9
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class BoltOptionTest {
    @Test
    public void test() {

        BoltOptions options = new BoltOptions();
        options.option(BoltRemotingOption.SERIALIZATION, Constants.DEFAULT_REMOTING_SERIALIZATION);

        Map<String, Object> map = options.options(BoltRemotingOption.class);

//        map.forEach((k, v) -> {
//            System.out.println(k.name() + ":" + k.defaultValue());
//            System.out.println(v);
//        });
//
//        map = options.options();
//
//        map.forEach((k, v) -> {
//            System.out.println(k.name() + ":" + k.defaultValue());
//            System.out.println(v);
//        });


    }

    public static void main(String[] args) {
        System.out.println(9&7);
    }
}
