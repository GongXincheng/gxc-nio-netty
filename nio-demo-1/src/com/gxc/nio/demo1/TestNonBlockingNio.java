package com.gxc.nio.demo1;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;

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
        SocketChannel sChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 10000));

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

    /**
     * 服务端
     */
    @Test
    public void server() throws Exception {
        // 1：获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2：切换到非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 3：绑定连接
        serverSocketChannel.bind(new InetSocketAddress(10000));
        // 4：获取选择器
        Selector selector = Selector.open();
        // 5：将通道注册到选择器上，并且指定"监听接收事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 6：轮询式的获取选择器上已经"准备就绪"的事件
        while (selector.select() > 0) {
            // 7：获取当前selector中所有注册的"选择键（已就绪的监听事件）"
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 8：获取准备"就绪"的事件
                SelectionKey selectionKey = iterator.next();
                // 9：判断具体是哪种事件类型
                if(selectionKey.isAcceptable()) {
                    // 10：若接收事件"就绪"，获取客户端连接
                    SocketChannel sChannel = serverSocketChannel.accept();
                    // 11：将客户端通道切换成非阻塞模式
                    sChannel.configureBlocking(false);
                    // 12：将该 客户端通道 注册到Selector上，监听"读就绪"状态
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    // 13：获取当前Selector上"读就绪"的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 14：读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (socketChannel.read(buffer) != -1) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, buffer.limit()));
                        buffer.clear();
                    }
                }
                // 15：取消选择键 SelectionKey
                iterator.remove();
            }
        }
    }
}
