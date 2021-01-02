package com.lee.http.boardcast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author Lee
 * @Date 2020/12/29
 */
public class LogHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent msg) throws Exception {
        System.out.printf("[%d] %s: %s%n", msg.getSendTime(), msg.getFilename(), msg.getLogString());
    }
}
