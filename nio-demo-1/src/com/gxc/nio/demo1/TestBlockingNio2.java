package com.gxc.nio.demo1;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 阻塞式NIO网络通讯(服务端相应客户端内容)
 *
 * @author GongXincheng
 * @date 2019-12-08 17:39
 */
public class TestBlockingNio2 {

    /**
     * 客户端
     */
    @Test
    public void client() throws Exception {
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 10000));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = FileChannel.open(
                Paths.get("1.png"), StandardOpenOption.READ);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }

        // 告诉服务端，客户端已经发完数据，否则服务端会一直处于阻塞状态
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


    /**
     * 服务端
     */
    @Test
    public void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(10000));

        SocketChannel socketChannel = serverSocketChannel.accept();
        FileChannel fileChannel = FileChannel.open(Paths.get("3.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }

        // 发送反馈给客户端
        buffer.put("服务端接收客户端数据成功".getBytes());
        buffer.flip();
        socketChannel.write(buffer);

        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }
}
