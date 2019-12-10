package com.gxc.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author GongXincheng
 * @date 2019-12-10 12:54
 */
public class MyServer {
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(10001));

        // 将通道注册到Selector，并监听"接收就绪"状态
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 开启一个线程监听 事件
        new Thread(new ListenerHandler(selector)).start();
    }

    /**
     * 事件监听器.
     */
    private static class ListenerHandler implements Runnable {

        private Selector selector;
        private Map<SocketChannel, String> channelMap = new ConcurrentHashMap<>(16);

        ListenerHandler(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = it.next();
                        it.remove();

                        // "接收就绪"
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                            System.out.println("|-- 服务端 ->\"接收就绪\"：" + socketChannel.getRemoteAddress());

                            // 必须为客户端通道添加此配置.
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);

                            // 将客户端通道，存到Map中
                            String channelName = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                            channelMap.put(socketChannel, channelName);
                        }

                        // "读就绪"
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            System.out.println("|-- 客户端\"读就绪\"：" + socketChannel.getRemoteAddress());

                            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
                            String message = "";
                            while (socketChannel.read(buffer) > 0) {
                                buffer.flip();
                                message = new String(buffer.array(), 0, buffer.limit());
                                System.out.println("服务端接收消息：" + message);
                                buffer.clear();
                            }

                            // TODO：服务端接收到消息后，处理消息，并返回数据
                            // 获取该通道的名称
                            String channelName = channelMap.get(socketChannel);

                            // 为多个通道发送该消息内容
                            for (Map.Entry<SocketChannel, String> entry : channelMap.entrySet()) {
                                SocketChannel channel = entry.getKey();
                                buffer.put(("[" + channelName + "]：" + message).getBytes());
                                buffer.flip();
                                try {
                                    channel.write(buffer);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
