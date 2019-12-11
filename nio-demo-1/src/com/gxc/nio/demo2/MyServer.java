package com.gxc.nio.demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
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

    private Selector selector;

    public static void main(String[] args) throws Exception {
        MyServer server = new MyServer();
        server.init(10001);
        server.listener();
    }

    /**
     * 服务端初始化。
     */
    private void init(int port) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 将通道注册到Selector，并监听"接收就绪"状态
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 监听器.
     */
    private void listener() {
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

                        // 1："接收就绪"
                        if (selectionKey.isAcceptable()) {
                            acceptHandler(selectionKey);
                        }
                        // 2："读就绪"
                        if (selectionKey.isReadable()) {
                            // 获取客户端连接
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                            // 读取客户端发给服务端的消息.
                            String message = readMessageFromChannel(selectionKey);

                            // TODO：必须加上此句，表示客户端关闭！否则会出现客户端无线读就绪循环
                            if (message.trim().length() == 0) {
                                channelMap.remove(socketChannel);
                                selectionKey.cancel();
                            } else {
                                // TODO：服务端将接收到的消息发送到所有已连接的客户端.
                                sendMessageToClient(socketChannel, message);
                            }
                        }  // "读就绪"
                    }// it.hasNext
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 服务端将接收到的消息发送到所有已连接的客户端.
         *
         * @param socketChannel 客户端连接
         * @param message       消息内容
         */
        private void sendMessageToClient(SocketChannel socketChannel, String message) throws IOException {
            // 获取该通道的名称
            String channelName = channelMap.get(socketChannel);

            // 为多个通道发送该消息内容
            for (Map.Entry<SocketChannel, String> entry : channelMap.entrySet()) {
                SocketChannel channel = entry.getKey();
                String msg = ("[" + channelName + "]：" + message);
                channel.write(ByteBuffer.wrap(msg.getBytes()));
            }
        }

        /**
         * 读取客户端发给服务端的消息.
         *
         * @param selectionKey SelectionKey.
         */
        private String readMessageFromChannel(SelectionKey selectionKey) throws Exception {
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

            return message;
        }

        /**
         * "接收就绪" 处理器
         *
         * @param selectionKey SelectionKey
         */
        private void acceptHandler(SelectionKey selectionKey) throws Exception {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            System.out.println("|-- 服务端 ->\"接收就绪\"：" + socketChannel.getRemoteAddress());

            // 必须为客户端通道添加此配置.
            socketChannel.configureBlocking(false);
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);

            // 将客户端通道，存到Map中
            String channelName = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            channelMap.put(socketChannel, channelName);
        }
    }


}
