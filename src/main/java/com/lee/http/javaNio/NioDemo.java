package com.lee.http.javaNio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author Lee
 * @Date 2021/1/3
 */
public class NioDemo {
    public static void main(String[] args) throws IOException {
        run();
    }

    public static void run() throws IOException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ServerSocket serverSocket = socketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8889));

        ByteBuffer msg = ByteBuffer.wrap("Hi, 沃日你妈妈妈妈\r\n".getBytes(StandardCharsets.UTF_8));
        while (true) {
            try {
                selector.select();
            } catch (Exception e) {
                e.printStackTrace();
                selector.close();
                break;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                try {
                    if (next.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                        SocketChannel client = channel.accept();
                        if (client != null) {
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, msg.duplicate());
                            System.out.println("Accept connect from " + client);
                        }
                    }
                    if (next.isWritable()) {
                        SocketChannel channel = (SocketChannel) next.channel();
                        ByteBuffer buffer = (ByteBuffer) next.attachment();
                        String sb = "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: text/plain;charset=utf-8\r\n" +
                                "\r\n" +
                                new String(buffer.array(), StandardCharsets.UTF_8);
                        channel.write(ByteBuffer.wrap(sb.getBytes(StandardCharsets.UTF_8)));
                        System.out.println("write");
                        channel.close();
                    }
                } catch (IOException ex) {
                    next.cancel();
                    try {
                        next.channel().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
