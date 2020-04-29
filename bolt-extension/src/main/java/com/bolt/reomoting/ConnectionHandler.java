package com.bolt.reomoting;

import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.exception.RemotingException;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/24
 * @Description: TODO
 */
public interface ConnectionHandler {

    ResponseFuture send(RequestCommand request) throws RemotingException;

    void send(ResponseCommand response) throws RemotingException;

}
