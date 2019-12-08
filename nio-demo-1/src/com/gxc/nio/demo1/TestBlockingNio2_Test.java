package com.gxc.nio.demo1;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 阻塞式NIO网络通讯(服务端相应客户端内容).
 *
 * @author GongXincheng
 * @date 2019-12-08 21:28
 */
public class TestBlockingNio2_Test {


    @Test
    public void client() throws Exception {
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 10000));

        FileChannel fileChannel = FileChannel.open(
                Paths.get("1.txt"), StandardOpenOption.READ);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }

        socketChannel.shutdownOutput();

        // 接收服务端反馈
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, buffer.limit()));
            buffer.clear();
        }

        fileChannel.close();
        socketChannel.close();
    }

    @Test
    public void server() throws Exception {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.bind(new InetSocketAddress(10000));

        SocketChannel socketChannel = ssChannel.accept();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("服务端接收到的消息：");
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            System.out.println(new String(buffer.array(), 0, buffer.limit()));
            buffer.clear();
        }
        System.out.println("=================");

        // 发送反馈给客户端
        buffer.put("服务端接收到消息".getBytes());
        buffer.flip();
        socketChannel.write(buffer);

        socketChannel.close();
        ssChannel.close();
    }

}
