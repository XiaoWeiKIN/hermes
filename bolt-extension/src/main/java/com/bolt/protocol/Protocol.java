package com.bolt.protocol;

import com.bolt.common.command.CommandCode;
import com.bolt.protocol.handler.CommandHandler;

import java.util.concurrent.ExecutorService;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
public interface Protocol {

    CommandHandler getCommandHandler(CommandCode cmdCode);

    ExecutorService getDefaultExecutor();

    void setExecutor(ExecutorService executor);


}
