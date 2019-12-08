package com.gxc.nio.demo1;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 非阻塞式NIO网络通讯
 *
 * @author GongXincheng
 * @date 2019-12-08 18:19
 */
public class TestNonBlockingNio {

    @Test
    public void client() throws Exception {
        // 1：获取Socket通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 10000));

        // 2：切换成非阻塞模式
        sChannel.configureBlocking(false);

        // 3：分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 4：发送数据给服务端
        buffer.put((LocalDateTime.now().toString() + "\n" + "Hello World").getBytes());
        buffer.flip();
        sChannel.write(buffer);
        buffer.clear();
        // 5：关闭通道
        sChannel.close();
    }

    @Test
    public void server() throws Exception {
        // 1：获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        // 2：切换为非阻塞模式
        ssChannel.configureBlocking(false);
        // 3：绑定连接
        ssChannel.bind(new InetSocketAddress(10000));
        // 4：获取选择器
        Selector selector = Selector.open();
        // 5：将通道注册到选择器上，并且指定"监听接收事件", (SelectionKey.OP_ACCEPT)监听通道的连接状态
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6：轮询式的获取选择器 已经准备就绪的事件
        while (selector.select() > 0) {
            // 7：获取当前选择器中所有注册的"选择键（已就绪的监听事件）"
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 8：获取准备"就绪"的事件
                SelectionKey selectedKey = iterator.next();
                // 9：判断具体是什么事件准备就绪
                if (selectedKey.isAcceptable()) {
                    // 10：若"接收就绪"，获取客户端连接
                    SocketChannel socketChannel = ssChannel.accept();
                    // 11：切换非阻塞模式
                    socketChannel.configureBlocking(false);
                    // 12：将该通道注册到选择器上
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectedKey.isReadable()) {
                    // 13：获取当前选择器上"读就绪"状态的通道
                    SocketChannel socketChannel = (SocketChannel) selectedKey.channel();

                    // 14：读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (socketChannel.read(buffer) != -1) {
                        buffer.flip();
                        System.out.println(
                                new String(buffer.array(), 0, buffer.limit()));
                        buffer.clear();
                    }
                }
                // 15：取消选择键(SelectionKey)
                iterator.remove();
            }
        }

    }


}
