package com.gxc.nio.demo2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

        // 开启一个线程监听 事件
        new Thread(new ListenerHandler(selector)).start();

        // 发送数据
        while (sc.hasNext()) {
            String message = sc.next();

            buffer.put(message.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }


    }

    /**
     * 事件监听器.
     */
    private static class ListenerHandler implements Runnable {

        private Selector selector;
        ListenerHandler(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            //System.out.println("[客户端] 接收到读状态：" + socketChannel.getRemoteAddress());
                            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
                            while (socketChannel.read(buffer) > 0) {
                                buffer.flip();
                                System.out.println(new String(buffer.array(), 0, buffer.limit()));
                                buffer.clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
