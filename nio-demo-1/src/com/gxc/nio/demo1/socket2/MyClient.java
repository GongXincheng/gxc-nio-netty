package com.gxc.nio.demo1.socket2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author GongXincheng
 * @date 2019-12-15 01:06
 */
public class MyClient {

    public void send() throws Exception {

        SocketChannel client = SocketChannel.open();

        client.configureBlocking(false);


        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9000);

        if (!client.connect(address)) {
            client.finishConnect();
        }

        client.write(ByteBuffer.wrap("admin".getBytes()));

        System.in.read();
//        client.shutdownOutput();
    }


    public static void main(String[] args) throws Exception {
        MyClient client = new MyClient();
        client.send();
    }
}
