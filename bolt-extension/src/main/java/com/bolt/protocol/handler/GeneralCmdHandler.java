package com.bolt.protocol.handler;

import com.bolt.common.DecodeableInvocation;
import com.bolt.common.Invocation;
import com.bolt.common.command.RemotingCommand;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.DefaultFuture;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.CommandCode;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.exception.RemotingException;
import com.bolt.protocol.processor.SyncUserProcessor;
import com.bolt.protocol.processor.UserProcessor;
import com.bolt.util.ObjectUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
public class GeneralCmdHandler extends AbstractCommandHandler {

    @Override
    public CommandCode getCommandCode() {
        return CommandCodeEnum.GENERAL_CMD;
    }

//    @Override
//    public void handle(RemotingContext ctx, RemotingCommand cmd) throws RemotingException {
//        if (cmd instanceof RequestCommand) {
//            RequestCommand request = (RequestCommand) cmd;
//            DecodeableInvocation inv = (DecodeableInvocation) request.getInvocation();
//            // IO thread decode className
//            inv.decodeClassName();
//            UserProcessor processor = processors.get(inv.getClassName());
//            if (processor == null) {
//                String errorMsg = "Registered UserProcessers to " + getClass().getSimpleName()
//                        + " handler, but no UserProcesser found by interest: " + inv.getClassName();
//                throw new RemotingException(ctx.getConnection(), errorMsg);
//            }
//        }
//
//        ExecutorService executor = processor.processInIOThread()
//                ? ctx.getEventLoop() : Optional.ofNullable(processor.getExecutor())
//                .orElseGet(() -> ctx.protocolExecutor());
//
//
//        try {
//            executor.execute(new HandlerRunnable(ctx, request));
//        } catch (Throwable e) {
//            throw new RemotingException(ctx.getConnection(), e);
//        }
//    }

    @Override
    public Object handleRequest(RemotingContext ctx, RequestCommand request) throws Exception {
        Invocation inv = request.getInvocation();
        UserProcessor processor =  processors.get(inv.getClassName());
        if (processor == null) {
            String errorMsg = "Registered UserProcessers to " + getClass().getSimpleName()
                    + " handler, but no UserProcesser found by interest: " + inv.getClassName();
            throw new RemotingException(ctx.getConnection(), errorMsg);
        }

        ExecutorService executor = processor.processInIOThread()
                ? ctx.getEventLoop() : Optional.ofNullable(processor.getExecutor())
                .orElseGet(() -> ctx.protocolExecutor());

        CompletableFuture.runAsync(() -> {
            try {

                return processor.handleRequest(inv.getData());
            } catch (Exception e) {
                throw new RemotingException(ctx.getConnection(), ObjectUtils.toString(e));
            }
        }, executor);


        return null;
    }

    @Override
    public void handleResponse(RemotingContext ctx, ResponseCommand response) throws Exception {
        Connection connection = ctx.getConnection();
        DefaultFuture.received(connection, response);
    }
}
