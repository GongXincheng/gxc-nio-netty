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
 * 阻塞式NIO网络通讯(传输图片)
 *
 * @author GongXincheng
 * @date 2019-12-08 17:20
 */
public class TestBlockingNio {

    @Test
    public void client() throws IOException {

        // 1：获取客户端Socket通道
        SocketChannel socketChannel = SocketChannel.open(
                new InetSocketAddress("127.0.0.1", 10000));

        // 2：创建本地文件通道，将文件写入到ByteBuffer缓冲区中
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        while (fileChannel.read(buffer) != -1) {
            // 将缓冲区的写模式切换为读模式
            buffer.flip();
            // 3：将缓冲区写入到Socket通道中
            socketChannel.write(buffer);
            buffer.clear();
        }

        // 4：关闭资源
        fileChannel.close();
        socketChannel.close();
        ;
    }

    @Test
    public void server() throws Exception {
        // 1：获取服务端Socket通道，并绑定端口号
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(10000));

        // 2：获取客户端Socket通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        // 3：创建本地文件通道
        FileChannel fileChannel = FileChannel.open(
                Paths.get("2.png"), StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);

        // 4：读取客户端通道中的数据到ByteBuffer中
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            // 4.1：将文件写入到本地文件通道中
            fileChannel.write(buffer);
            buffer.clear();
        }

        // 5：关闭资源
        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }

}
