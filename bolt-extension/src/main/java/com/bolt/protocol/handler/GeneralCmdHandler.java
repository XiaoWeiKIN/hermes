package com.bolt.protocol.handler;

import com.bolt.common.DecodeableInvocation;
import com.bolt.common.Invocation;
import com.bolt.common.command.RemotingCommand;
import com.bolt.common.extension.ExtensionLoader;
import com.bolt.reomoting.Connection;
import com.bolt.reomoting.DefaultFuture;
import com.bolt.reomoting.RemotingContext;
import com.bolt.common.command.CommandCode;
import com.bolt.common.command.RequestCommand;
import com.bolt.common.command.ResponseCommand;
import com.bolt.common.enums.CommandCodeEnum;
import com.bolt.common.exception.RemotingException;
import com.bolt.protocol.processor.UserProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/19
 * @Description: TODO
 */
public class GeneralCmdHandler extends AbstractCommandHandler {
    private ConcurrentHashMap<String, UserProcessor<?>> processors = new ConcurrentHashMap<String, UserProcessor<?>>(4);

    public GeneralCmdHandler() {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(UserProcessor.class).getSupportedExtensions();
        for (String name : supportedExtensions) {
            UserProcessor processor = ExtensionLoader.getExtensionLoader(UserProcessor.class).getExtension(name);
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
    }


//    private void registerProcessor(UserProcessor processor) {
//        ObjectUtils.isNotNull(processor, "Processer should benot null");
//        if (!StringUtils.isNotBlank(processor.interest())) {
//            throw new IllegalArgumentException("Processor interest should not be blank!");
//        }
//        UserProcessor<?> preProcessor = processors.putIfAbsent(processor.interest(),
//                processor);
//        if (preProcessor != null) {
//            String errMsg = "Processor with interest key ["
//                    + processor.interest()
//                    + "] has already been registered, can not register again!";
//            throw new IllegalArgumentException(errMsg);
//        }
//    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCodeEnum.GENERAL_CMD;
    }

    @Override
    public void handle(RemotingContext ctx, RemotingCommand cmd) throws RemotingException {
        UserProcessor processor = null;
        ExecutorService executor = null;
        DecodeableInvocation inv = (DecodeableInvocation) cmd.getInvocation();
        // IO thread decode className
        inv.decodeClassName();

        if (cmd instanceof RequestCommand) {
            processor = processors.get(inv.getClassName());
            if (processor == null) {
                String errorMsg = "Registered UserProcessers to " + getClass().getSimpleName()
                        + " handler, but no UserProcesser found by interest: " + inv.getClassName();
                throw new RemotingException(ctx.getConnection(), errorMsg);
            }
            executor = processor.processInIOThread()
                    ? ctx.getEventLoop() : Optional.ofNullable(processor.getExecutor())
                    .orElseGet(() -> ctx.protocolExecutor());

        } else if (cmd instanceof ResponseCommand) {
            executor = handelInIOThread()
                    ? ctx.getEventLoop() : ctx.protocolExecutor();
        }
        try {
            executor.execute(new HandlerRunnable(ctx, cmd));
        } catch (Throwable e) {
            throw new RemotingException(ctx.getConnection(), e);
        }
    }

    @Override
    public Object handleRequest(RemotingContext ctx, RequestCommand request) throws Exception {
        Invocation inv = request.getInvocation();
        UserProcessor processor = processors.get(inv.getClassName());
        return processor.handleRequest(inv.getData());
    }

    @Override
    public boolean handelInIOThread() {
        return false;
    }
}
