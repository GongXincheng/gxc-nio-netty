package com.gxc.nio.demo1.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author GongXincheng
 * @date 2019-12-10 12:54
 */
public class MyClient {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 10001));

        socketChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

        while (sc.hasNext()) {
            String message = sc.next();

            buffer.put(message.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }



    }

}
