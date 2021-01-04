package com.lee.http.multicast;

import com.lee.http.boardcast.LogEncoder;
import com.lee.http.boardcast.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lee
 * @Date 2021/1/4
 */
public class LogEventMulticast {

    private NetworkInterface ni;
    private InetAddress localAddress;
    private final InetSocketAddress groupAddress;
    private Bootstrap bootstrap;
    private EventLoopGroup group;

    public LogEventMulticast(InetSocketAddress groupAddress) {
        this.groupAddress = groupAddress;
        init();
    }

    public void init() {
        // get local address
        setNetInterface();
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channelFactory(() -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                .localAddress(localAddress, 8888)
                .option(ChannelOption.IP_MULTICAST_IF, ni)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new LogEncoder(groupAddress));
    }

    public void run() {
        try {
            NioDatagramChannel channel = (NioDatagramChannel) bootstrap.bind().sync().channel();
            channel.joinGroup(groupAddress, ni).sync();
            System.out.println("Server start");
            while (true) {
                channel.writeAndFlush(new LogEvent("name", "hello multicast"));
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    private void setNetInterface() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getName().equals("wlan2")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof Inet4Address) {
                            localAddress = inetAddress;
                            break;
                        }
                    }
                    ni = networkInterface;
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("No interfaces exist", e);
        }
    }

    public static void main(String[] args) {
        LogEventMulticast multicast = new LogEventMulticast(new InetSocketAddress("234.0.0.100", 7777));
        multicast.run();
    }

}
