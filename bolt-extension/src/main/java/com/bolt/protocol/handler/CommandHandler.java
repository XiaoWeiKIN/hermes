package com.bolt.protocol.handler;

import com.bolt.common.exception.RemotingException;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.CommandCode;
import com.bolt.common.extension.Extension;


/**
 * @Author: wangxw
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
@Extension
public interface CommandHandler<T> {

    CommandCode getCommandCode();

    void handle(RemotingContext ctx, T command) throws RemotingException;

    boolean handelInIOThread();
}
