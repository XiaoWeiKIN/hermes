package com.bolt.common;

import com.bolt.reomoting.Connection;
import com.bolt.util.CountDownLatchUtil;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/21
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class NettyAttrTest {
    @Test
    public void test() throws InterruptedException {
        AttributeKey<Connection> CON = AttributeKey.valueOf("CONNECTION");
        io.netty.channel.Channel ch = new NioSocketChannel();

        CountDownLatchUtil latchUtil = new CountDownLatchUtil();

        for(int i=0;i<5;i++){
            Connection con = new Connection(ch);
            System.out.println("插入："+con);
            ch.attr(CON).setIfAbsent(con);
        }


        Connection connection = ch.attr(CON).get();
        System.out.println(connection);
    }

    @Test
    public void test1(){
        Channel ch = new NioSocketChannel();
        ch.close();
        ch.close();
    }
}
