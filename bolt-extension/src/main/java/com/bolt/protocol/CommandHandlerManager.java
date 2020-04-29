package com.bolt.protocol;

import com.bolt.common.command.CommandCode;
import com.bolt.common.extension.ExtensionLoader;
import com.bolt.protocol.processor.UserProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Author: wangxw
 * @DateTime: 2020/4/4
 * @Description: TODO
 */
public class CommandHandlerManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandlerManager.class);
    private static final ConcurrentHashMap<CommandCode, CommandHandler<?>> CMD_HANDLER_MAP = new ConcurrentHashMap(4);

    static {
        Set<String> supportedExtensions = ExtensionLoader.getExtensionLoader(CommandHandler.class).getSupportedExtensions();
        for (String name : supportedExtensions) {
            CommandHandler commandHandler = ExtensionLoader.getExtensionLoader(CommandHandler.class).getExtension(name);
            CommandCode cmdCode = commandHandler.getCommandCode();
            if (CMD_HANDLER_MAP.get(cmdCode) != null) {
                logger.error("CommandHandler extension " + commandHandler.getClass().getName()
                        + " has duplicate version to Protocol extension "
                        + CMD_HANDLER_MAP.get(cmdCode).getClass().getName()
                        + ", ignore this CommandHandler extension");
                continue;
            }
            CMD_HANDLER_MAP.put(cmdCode, commandHandler);
            if (logger.isDebugEnabled()) {
                logger.debug("Register cmdCode [" + cmdCode + "] with CommandHandler [" + commandHandler.getClass().getName() + "]");
            }
        }
        supportedExtensions = ExtensionLoader.getExtensionLoader(UserProcessor.class).getSupportedExtensions();
        for (String name : supportedExtensions) {
            UserProcessor processor = ExtensionLoader.getExtensionLoader(UserProcessor.class).getExtension(name);
            CommandHandler<?> handler = CMD_HANDLER_MAP.get(processor.cmdCode());
            if (handler == null) {
                logger.error("CommandHandler not exist with processer interest key " + processor.interest());
                continue;
            }
            handler.registerProcessor(processor);
        }

    }

    public CommandHandler getCmdHandler(CommandCode cmdCode) {
        return Optional.ofNullable(CMD_HANDLER_MAP.get(cmdCode))
                .orElseThrow(() -> new IllegalArgumentException("CmdCode " + cmdCode + " no register CommandHandler"));
    }

}
