package com.gxc.nio.demo1;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * UDP NIO
 * @author GongXincheng
 * @date 2019-12-08 23:38
 */
public class TestNonBlockingNio2 {

    /**
     * 发送端.
     */
    @Test
    public void send() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String message = sc.next();
            buffer.put(message.getBytes());

            buffer.flip();
            datagramChannel.send(buffer,
                    new InetSocketAddress("127.0.0.1", 10001));
            buffer.clear();
        }

        sc.close();
        datagramChannel.close();
    }

    /**
     * 接收端
     */
    @Test
    public void receive() throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(10001));

        Selector selector = Selector.open();

        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {
            Set<SelectionKey> skSet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = skSet.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                if (sk.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    datagramChannel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(
                            buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
                iterator.remove();
            }
        }

    }

}
