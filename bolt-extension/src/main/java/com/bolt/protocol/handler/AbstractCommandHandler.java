package com.bolt.protocol.handler;

import com.bolt.common.DecodeableInvocation;
import com.bolt.common.Invocation;
import com.bolt.common.enums.ResponseStatus;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.*;
import com.bolt.common.exception.RemotingException;
import com.bolt.protocol.CommandHandler;
import com.bolt.protocol.processor.UserProcessor;
import com.bolt.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
public abstract class AbstractCommandHandler<T extends RemotingCommand> implements CommandHandler<T> {
    protected ConcurrentHashMap<String, UserProcessor<?>> processors = new ConcurrentHashMap<String, UserProcessor<?>>(4);
    protected CommandFactory commandFactory;

    public AbstractCommandHandler() {
        this.commandFactory = new CommandFactory();
    }

    @Override
    public void registerProcessor(UserProcessor processor) {
        ObjectUtils.isNotNull(processor, "Processer should benot null");
        if (!StringUtils.isNotBlank(processor.interest())) {
            throw new IllegalArgumentException("Processor interest should not be blank!");
        }
        UserProcessor<?> preProcessor = processors.putIfAbsent(processor.interest(),
                processor);
        if (preProcessor != null) {
            String errMsg = "Processor with interest key ["
                    + processor.interest()
                    + "] has already been registered, can not register again!";
            throw new IllegalArgumentException(errMsg);
        }
    }

    @Override
    public boolean handelInIOThread() {
        return true;
    }

    @Override
    public void handle(RemotingContext ctx, T cmd) throws RemotingException {
        ExecutorService executor = handelInIOThread()
                ? ctx.getEventLoop() : ctx.protocolExecutor();
        // IO thread decode className
        DecodeableInvocation inv = (DecodeableInvocation) cmd.getInvocation();
        inv.decodeClassName();

        try {
            executor.execute(new HandlerRunnable(ctx, cmd));
        } catch (Exception e) {

        }


    }

    protected void sendResponseIfNecessary(RequestCommand request) {

    }

    class HandlerRunnable implements Runnable {
        private RemotingContext context;
        private T command;

        public HandlerRunnable(RemotingContext context, T command) {
            this.context = context;
            this.command = command;
        }

        @Override
        public void run() {

            DecodeableInvocation invocation = (DecodeableInvocation) command.getInvocation();
            // IO thread or biz thread decode data
            invocation.decodeData();
            try {
                if (command instanceof RequestCommand) {
                    RequestCommand request = (RequestCommand) command;
                    if (request.isBroken()) {
                        Throwable t = (Throwable) invocation.getData();
                        ResponseCommand response = commandFactory.createResponse(request, ResponseStatus.SERVER_EXCEPTION, ObjectUtils.toString(t)
                        );
                        context.writeAndFlush(response);
                        return;
                    }
                    try {
                        Object result = handleRequest(context, request);
                        // 双向请求
                        if (request.isTwoWay()) {
                            ResponseCommand response = commandFactory.createResponse(request, result);
                            context.writeAndFlush(response);
                        }
                    } catch (Throwable t) {
                        commandFactory.createResponse(request, ResponseStatus.SERVER_EXCEPTION, ObjectUtils.toString(t));
                    }

                } else if (command instanceof ResponseCommand) {
                    handleResponse(context, (ResponseCommand) command);
                }
            } catch (Exception e) {

            }
        }
    }

    public abstract Object handleRequest(RemotingContext ctx, RequestCommand cmd) throws Exception;

    public abstract void handleResponse(RemotingContext ctx, ResponseCommand cmd) throws Exception;

}
