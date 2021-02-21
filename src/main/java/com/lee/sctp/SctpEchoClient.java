package com.lee.sctp;

/**
 * @Author Lee
 * @Date 2021/2/21
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.channel.sctp.nio.NioSctpChannel;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server over SCTP connection.
 * <p>
 * Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class SctpEchoClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            startConnect(group);
            disConnect(group);
            startConnect(group);
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    private static void disConnect(EventLoopGroup group) {
        group.shutdownGracefully();
    }

    private static void startConnect(EventLoopGroup group) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSctpChannel.class)
                .option(SctpChannelOption.SCTP_NODELAY, true)
                .handler(new ChannelInitializer<SctpChannel>() {
                    @Override
                    public void initChannel(SctpChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                //new LoggingHandler(LogLevel.INFO),
                                new SctpEchoClientHandler());
                    }
                });

        // Start the client.
        ChannelFuture f = b.connect(HOST, PORT).sync();
        // Wait until the connection is closed.
        f.channel().closeFuture().sync();
    }
}
