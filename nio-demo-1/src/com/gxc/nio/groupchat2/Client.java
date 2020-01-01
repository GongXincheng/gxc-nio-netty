package com.gxc.nio.groupchat2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GongXincheng
 * @date 2020-01-01 20:13
 */
public class Client {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9000;
    private Selector selector;
    private SocketChannel clientChannel;
    private static ExecutorService threadPool = Executors.newCachedThreadPool();


    private Client() throws Exception {
        selector = Selector.open();
        // 连接服务器
        clientChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 监听通道事件.
     */
    private void listener() {
        threadPool.execute(() -> {
            try {
                while (selector.select() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();

                        // 读就绪
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            String message = this.getMessageFromClient(channel);

                            System.out.println(message);
                        }
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 获取客户端发送的内容.
     *
     * @param clientChannel 客户端通道.
     * @return message
     */
    private String getMessageFromClient(SocketChannel clientChannel) throws Exception {
        StringBuilder strBuilder = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        while (clientChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            strBuilder.append(new String(byteBuffer.array(), 0, byteBuffer.limit()));
            byteBuffer.clear();
        }
        return strBuilder.toString();
    }

    /**
     * 发送消息.
     *
     * @param message message
     */
    private void sendMessage(String message) throws Exception {
        clientChannel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }


    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        Client client = new Client();
        client.listener();

        String message;
        do {
            message = scanner.next();
            if (message != null && message.trim().length() > 0) {
                client.sendMessage(message);
            }
        } while (!Objects.equals(message, "886"));

        scanner.close();
        threadPool.shutdownNow();
    }

}
