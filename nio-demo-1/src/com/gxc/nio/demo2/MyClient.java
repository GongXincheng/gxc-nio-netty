package com.gxc.nio.demo2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 客户端.
 *
 * @author GongXincheng
 * @date 2019-12-10 12:54
 */
public class MyClient {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private Selector selector;

    private SocketChannel socketChannel;

    /**
     * 客户端启动.
     */
    public static void main(String[] args) throws Exception {
        MyClient client = new MyClient();
        //client.init("127.0.0.1", 10001);
        client.init("192.168.14.101", 1234);
        client.listener();
        client.sendMessage();
    }

    /**
     * 初始化 客户端通道
     *
     * @param host 服务端i
     * @param port 端口号
     */
    private void init(String host, int port) throws Exception {
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 客户端通道监听.
     */
    private void listener() {
        // 开启一个线程监听 事件
        new Thread(new ListenerHandler(selector)).start();
    }

    private void sendMessage() throws Exception {
        Scanner sc = new Scanner(System.in);
        // 发送数据
        while (sc.hasNext()) {
            String message = sc.next();
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
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
