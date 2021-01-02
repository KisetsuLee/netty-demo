package com.lee.http.boardcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @Author Lee
 * @Date 2020/12/29
 */
public class LogDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
        int i = content.indexOf(0, content.readableBytes(), LogEvent.SEPARATOR);
        long timestamp = Long.parseLong(content.slice(0, i).toString(CharsetUtil.UTF_8));
        int i1 = content.indexOf(i + 1, content.readableBytes(), LogEvent.SEPARATOR);
        String filename = content.slice(i + 1, i1 - i - 1).toString(CharsetUtil.UTF_8);
        String log = content.slice(i1 + 1, content.readableBytes() - i1 - 1).toString(CharsetUtil.UTF_8);
        LogEvent logEvent = new LogEvent(filename, log, timestamp);
        out.add(logEvent);
    }
}
