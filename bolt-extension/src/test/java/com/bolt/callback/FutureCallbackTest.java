package com.bolt.callback;

import com.bolt.common.Invocation;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.DefaultFuture;
import com.bolt.reomoting.FutureAdapter;
import com.bolt.reomoting.ResponseCallback;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/28
 * @Description: TODO
 */
@RunWith(JUnit4.class)
public class FutureCallbackTest {

    @Test
    public void callbcak() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Channel channel = new NioSocketChannel();
        Connection connection = new Connection(channel);
        RequestCommand req = new RequestCommand(CommandCodeEnum.GENERAL_CMD);
        int id = req.getId();
        DefaultFuture future = DefaultFuture.newFuture(connection, req
                , 1000);

        future.setCallback(new ResponseCallback() {
            @Override
            public void done(Invocation response) {
                latch.countDown();

                System.out.println("callback");
            }


            @Override
            public void caught(Throwable exception) {
            }
        });
        ResponseCommand res = new ResponseCommand(id, CommandCodeEnum.GENERAL_CMD);
        res.setInvocation(new Invocation());
        res.setStatus(ResponseStatus.SUCCESS);
        DefaultFuture.received(connection, res);
        latch.await();
    }

    @Test
    public void callbackWithCom() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Channel channel = new NioSocketChannel();
        Connection connection = new Connection(channel);
        RequestCommand req = new RequestCommand(CommandCodeEnum.GENERAL_CMD);
        int id = req.getId();
        DefaultFuture future = DefaultFuture.newFuture(connection, req
                , 1000);
        FutureAdapter<String> futureAdapter = new FutureAdapter<String>(future);

        ResponseCommand res = new ResponseCommand(id, CommandCodeEnum.GENERAL_CMD);
        Invocation invocation = new Invocation();
        invocation.setData("---name---");
        res.setInvocation(invocation);
        res.setStatus(ResponseStatus.SUCCESS);
        DefaultFuture.received(connection, res);


        futureAdapter.whenComplete((k, v) -> {
            latch.countDown();
            System.out.println(k);
        });

        latch.await();

    }

    @Test
    public void completable_future_test() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            return "test";
        });

        completableFuture.whenComplete((k,v)->{

        });
    }
}
