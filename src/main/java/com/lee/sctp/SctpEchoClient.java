package com.lee.sctp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server over SCTP connection.
 * <p>
 * Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 *
 * @Author Lee
 * @Date 2021/2/21
 */
public final class SctpEchoClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    static final HashMap<String, Channel> channelMap = new HashMap<>();
    static EventLoopGroup group = null;
    static SocketAddress addr = null;


    public static void main(String[] args) throws Exception {
        try {
            System.out.println("client connect");
            startConnect();
            TimeUnit.SECONDS.sleep(10);
            System.out.println("client disconnect");
            disConnect();
            TimeUnit.SECONDS.sleep(10);
            System.out.println("client reconnect");
            startConnect();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    private static void disConnect() {
        group.shutdownGracefully();
    }

    private static void startConnect() throws InterruptedException {
        // Configure the client.
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSctpChannel.class)
                .option(SctpChannelOption.SCTP_NODELAY, true)
                .handler(new ChannelInitializer<SctpChannel>() {
                    @Override
                    public void initChannel(SctpChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO), new SctpEchoClientHandler());
                    }
                });
        if (addr != null) {
            b.localAddress(addr);
        }
        Channel channel = b.connect(HOST, PORT).sync().channel();
        addr = channel.localAddress();
    }
}
