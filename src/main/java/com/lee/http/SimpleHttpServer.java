package com.lee.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Author Lee
 * @Date 2020/12/9
 */
public class SimpleHttpServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加handler
                        ch.pipeline().addLast(new HttpServerCodec())
                                .addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        if (msg instanceof HttpRequest) {
                                            HttpRequest httpRequest = (HttpRequest) msg;
                                            System.out.printf("收到来自%s客户端的消息%n", ctx.channel().remoteAddress());
                                            System.out.printf("客户端请求资源路径%s%n", httpRequest.uri());
                                            // 回复
                                            ByteBuf content = Unpooled.copiedBuffer("worinima", CharsetUtil.UTF_8);
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                                            ctx.writeAndFlush(response);
                                        }
                                    }
                                });
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(3000).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
