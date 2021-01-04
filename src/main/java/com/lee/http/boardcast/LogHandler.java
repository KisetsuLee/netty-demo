package com.lee.http.boardcast;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Lee
 * @Date 2020/12/29
 */
@ChannelHandler.Sharable
public class LogHandler extends SimpleChannelInboundHandler<LogEvent> {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent msg) throws Exception {

        System.out.println("Current channel local address is " + ctx.channel().localAddress());

//        ctx.channel().eventLoop().execute(() -> executorService.execute(() -> {
//            try {
//                Thread.sleep(10 * 1000);
//                System.out.println("1 -- Current thread is " + Thread.currentThread().getName());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }));
//        ctx.channel().eventLoop().execute(() -> executorService.execute(() -> {
//            try {
//                Thread.sleep(10 * 1000);
//                System.out.println("2 -- Current thread is " + Thread.currentThread().getName());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }));
//        ctx.channel().eventLoop().execute(() -> {
//            try {
//                Thread.sleep(10 * 1000);
//                System.out.println("1 -- Current thread is " + Thread.currentThread().getName());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        ctx.channel().eventLoop().execute(() -> {
//            try {
//                Thread.sleep(10 * 1000);
//                System.out.println("2 -- Current thread is " + Thread.currentThread().getName());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
        System.out.printf("[%s] %s: %s%n", format(msg.getSendTime()), msg.getFilename(), msg.getLogString());
    }

    private String format(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }
}
