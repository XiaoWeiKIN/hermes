package com.bolt.protocol;

import com.bolt.common.command.CommandCode;
import com.bolt.protocol.handler.CommandHandler;
import com.bolt.protocol.handler.CommandHandlerManager;
import com.bolt.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
public class BoltProtocol implements Protocol {
    private final CommandHandlerManager cmdHandlerManager;
    private ExecutorService executor;

    public BoltProtocol() {
        this.cmdHandlerManager = new CommandHandlerManager();

        this.executor = new ThreadPoolExecutor(8,
                16,
                20,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(60),
                new NamedThreadFactory("Bolt-protocol-executor", true));
    }

    @Override
    public CommandHandler getCommandHandler(CommandCode cmdCode) {
        return this.cmdHandlerManager.getCmdHandler(cmdCode);
    }

    @Override
    public ExecutorService getDefaultExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

}
