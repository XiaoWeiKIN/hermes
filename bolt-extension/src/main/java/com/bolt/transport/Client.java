package com.bolt.transport;

import com.bolt.common.Url;
import com.bolt.common.exception.RemotingException;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/22
 * @Description: TODO
 */
public interface Client extends Endpoint {

    void reconnect() throws RemotingException;

    <T> T request(Url url, Object request);


}
