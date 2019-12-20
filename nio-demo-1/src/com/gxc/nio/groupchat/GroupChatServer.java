package com.gxc.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 群聊服务端
 *
 * @author GongXincheng
 * @date 2019-12-15 06:39
 */
public class GroupChatServer {

    private static final int PORT = 6667;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    /**
     * 构造器，初始化工作
     */
    public GroupChatServer() {
        try {
            // 得到选择器
            selector = Selector.open();

            // 得到 ServerSocketChannel
            serverChannel = ServerSocketChannel.open();
            // 绑定端口
            serverChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置为非阻塞
            serverChannel.configureBlocking(false);
            // 将服务端通道注册到Selector上，并设置"读就绪"事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("|---------- 服务端初始化完成 ----------|");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通道时间监听
     */
    private void listener() throws Exception {

        while (selector.select() > 0) {
            try {
                // TODO：可否在此处单独使用一个线程完成?
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 防止重复处理，移除SelectionKey
                    iterator.remove();

                    // "接收就绪"事件
                    if (selectionKey.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        // 提示
                        System.out.println(String.format("|==> %s 上线", clientChannel.getRemoteAddress()));

                        int clientNum = selector.keys().size() - 1;
                        String clientUsername = clientChannel.getRemoteAddress().toString().substring(1);
                        String message = clientUsername + " 进入聊天室, 当前人数(" + clientNum + ")";
                        sendInfoToOtherClients(message, clientChannel);
                    }
                    // "读就绪"事件
                    if (selectionKey.isReadable()) {
                        // 处理读取的内容
                        readData(selectionKey);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 发生异常处理
                try {
                    serverChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 读取客户端消息
     */
    private void readData(SelectionKey selectionKey) {
        // 定义一个SocketChannel
        SocketChannel clientChannel = null;
        try {
            // 得到 客户端Channel
            clientChannel = (SocketChannel) selectionKey.channel();
            // 创建Buffer
            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

            // 读取数据
            StringBuilder msg = new StringBuilder();
            while (clientChannel.read(buffer) > 0) {
                buffer.flip();
                msg.append(new String(buffer.array(), 0, buffer.limit()));
                buffer.clear();
            }

            // TODO：必须加上此判断，否则其中一个客户端关闭，导致服务端死循环 CPU:100%
            if (msg.toString().trim().length() == 0) {
                System.out.println(String.format("|==> %s 离线", clientChannel.getRemoteAddress()));
                sendInfoToOtherClients(clientChannel.getRemoteAddress().toString().substring(1) + " 退出聊天室, 当前人数(" + (selector.keys().size() - 2) + ")", clientChannel);
                // 取消注册
                selectionKey.cancel();
                // 关闭通道
                clientChannel.close();
                return;
            }

            System.out.println("|----- 服务端收到客户端发送的消息：" + msg.toString());
            // TODO：将消息转发给其他客户端
            sendInfoToOtherClients(msg.toString(), clientChannel);
        } catch (Exception e) {
            try {
                assert clientChannel != null;
                System.out.println(String.format("|==> %s 离线", clientChannel.getRemoteAddress()));
                // 取消注册
                selectionKey.cancel();
                // 关闭通道
                clientChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 将消息转发给其他客户端（需要去掉自己）
     *
     * @param msg  消息
     * @param self 当前发消息的客户端Channel（为了去除自己）
     */
    private void sendInfoToOtherClients(String msg, SocketChannel self) throws Exception {
        // 遍历所有注册到select上的SocketChannel，并排除 self
        for (SelectionKey selectionKey : selector.keys()) {
            Channel targetChannel = selectionKey.channel();

            // 排除自己的Channel
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                SocketChannel target = (SocketChannel) targetChannel;
                // 消息发送出去
                target.write(ByteBuffer.wrap(msg.getBytes()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        GroupChatServer server = new GroupChatServer();
        server.listener();
    }

}
