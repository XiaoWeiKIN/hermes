//package com.bolt.protocol.handler;
//
//import com.bolt.reomoting.RemotingContext;
//import com.bolt.common.command.CommandCode;
//import com.bolt.common.command.ResponseCommand;
//import com.bolt.common.enums.CommandCodeEnum;
//import com.bolt.reomoting.Connection;
//import com.bolt.reomoting.DefaultFuture;
//
///**
// * @Author: wangxw
// * @DateTime: 2020/4/20
// * @Description: TODO
// */
//public class GeneralResCmdHandler extends AbstractCommandHandler<ResponseCommand> {
//
//    @Override
//    public CommandCode getCommandCode() {
//        return CommandCodeEnum.GENERAL_CMD_RES;
//    }
//
//    @Override
//    public void doHandle(RemotingContext ctx, ResponseCommand responseCommand) {
//        Connection connection = ctx.getConnection();
//        DefaultFuture.received(connection, responseCommand);
//    }
//}
