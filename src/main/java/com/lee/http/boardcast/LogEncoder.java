package com.lee.http.boardcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author Lee
 * @Date 2020/12/28
 */
public class LogEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress address;

    public LogEncoder(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent msg, List<Object> out) {
        byte[] timestamp = String.valueOf(msg.getSendTime()).getBytes(CharsetUtil.UTF_8);
        byte[] filename = msg.getFilename().getBytes(CharsetUtil.UTF_8);
        byte[] logString = msg.getLogString().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(timestamp.length + filename.length + logString.length + 2);
        buf.writeBytes(timestamp).writeByte(LogEvent.SEPARATOR).writeBytes(filename).writeByte(LogEvent.SEPARATOR).writeBytes(logString);
        out.add(new DatagramPacket(buf, address));
    }
}
