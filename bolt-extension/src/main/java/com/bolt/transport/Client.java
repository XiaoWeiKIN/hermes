package com.bolt.transport;

import com.bolt.common.Url;
import com.bolt.common.exception.RemotingException;
import com.bolt.reomoting.Connection;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/22
 * @Description: TODO
 */
public interface Client extends Endpoint {

    Connection ctreateConnectionIfAbsent(Url url) throws RemotingException;


    <T> T request(Url url, Object request) throws RemotingException;
}
