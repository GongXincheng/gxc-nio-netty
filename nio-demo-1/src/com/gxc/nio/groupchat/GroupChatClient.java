package com.gxc.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 群聊客户端.
 *
 * @author GongXincheng
 * @date 2019-12-15 07:45
 */
public class GroupChatClient {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6667;
    private Selector selector;
    private SocketChannel clientChannel;
    private String username;

    /**
     * 构造器
     */
    private GroupChatClient() throws IOException {
        selector = Selector.open();
        // 连接服务器
        clientChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        // 得到Username
        username = clientChannel.getLocalAddress().toString().substring(1);
        System.out.println("|---- 客户端 " + username + " 初始化完成 ----|");
    }

    /**
     * 发送消息给服务端
     *
     * @param info 消息内容
     */
    public void sendInfo(String info) {
        info = "====== " + username + " ====== \n" + info;
        try {
            clientChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取服务端回复的消息
     */
    public void readInfo() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    // "读就绪事件"
                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

                        StringBuilder info = new StringBuilder();
                        while (channel.read(buffer) > 0) {
                            buffer.flip();
                            info.append(new String(buffer.array(), 0, buffer.limit()));
                            buffer.clear();
                        }
                        System.out.println(info.toString());
                    }
                }
            } // (selector.select() > 0)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        GroupChatClient client = new GroupChatClient();

        // 启动一个线程
        new Thread(() -> client.readInfo()).start();

        // 发送数据
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String message = sc.next();
            client.sendInfo(message);
        }
    }
}
