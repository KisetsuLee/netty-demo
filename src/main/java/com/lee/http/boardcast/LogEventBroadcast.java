package com.lee.http.boardcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lee
 * @Date 2020/12/28
 */
public class LogEventBroadcast {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;
    private final InetSocketAddress address = new InetSocketAddress("225.0.0.100", 9999);

    public LogEventBroadcast(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
//        Channel channel = bootstrap.bind(new InetSocketAddress("192.168.0.107", 8888)).sync().channel();
        Channel channel = bootstrap.bind("127.0.0.1", 8888).sync().channel();
        long pt = 0;
        while (true) {
            if (pt > file.length()) {
                // reset
                pt = 0;
            } else if (pt < file.length()) {
                try (RandomAccessFile accessFile = new RandomAccessFile(this.file, "r")) {
                    accessFile.seek(pt);
                    String line;
                    while ((line = accessFile.readLine()) != null) {
                        channel.writeAndFlush(new LogEvent(this.file.getName(), line));
                        System.out.println(line);
                    }
                    pt = accessFile.getFilePointer();
                }
            }
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        LogEventBroadcast logEventBroadcast = new LogEventBroadcast(
                new InetSocketAddress("255.255.255.255", 9999),
                new File("C:\\Users\\56367\\Desktop\\1.txt")
        );
        try {
            logEventBroadcast.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logEventBroadcast.stop();
        }
    }
}
