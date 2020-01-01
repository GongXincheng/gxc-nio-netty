package com.gxc.nio.groupchat2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author GongXincheng
 * @date 2020-01-01 19:32
 */
public class Server {

    private static final int PORT = 9000;
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private ServerSocketChannel serverChannel;
    private Selector selector;

    /**
     * 初始化配置
     */
    private Server() throws Exception {
        // 创建Selector
        selector = Selector.open();
        // 创建服务端通道 ServerSocketChannel
        serverChannel = ServerSocketChannel.open();
        // 将通道设置为非阻塞
        serverChannel.configureBlocking(false);
        // 为通道设置端口绑定
        serverChannel.bind(new InetSocketAddress(PORT));
        // 将该通道注册到Selector(多路复用器)上，监听"接收就绪"状态
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println(String.format("|-- 服务端启动成功, ip：[%s] port：[%s] --|", serverChannel.getLocalAddress().toString(), PORT));
    }


    /**
     * 监听通道状态
     */
    private void listener() throws Exception {

        while (selector.select() > 0) {
            try {
                // 获取 SelectionKey 集合并处理
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    // 接收就绪
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        // 服务端通道接收客户端通道
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        // 发送消息给其他通道
                        sendMessageToOther(socketChannel.getRemoteAddress().toString().substring(1) + " 上线啦！", socketChannel);
                    }

                    // 读就绪
                    if (selectionKey.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();

                        String message = this.getMessageFromClient(clientChannel);
                        if (message.trim().length() == 0) {
                            this.sendMessageToOther(String.format("|-- %s 离开了聊天室! --|", clientChannel.getRemoteAddress().toString()), clientChannel);
                            // 取消注册 & 关闭通道
                            selectionKey.cancel();
                            clientChannel.close();
                            break;
                        }

                        // 发送该消息到其他客户端
                        this.sendMessageToOther(String.format("|--> %s发送消息：%s", clientChannel.getRemoteAddress().toString(), message), clientChannel);
                    }

                    // 防止事件被重复处理
                    iterator.remove();
                }
            } catch (Exception e) {
                selector.close();
                serverChannel.close();
                System.err.println("服务端处理异常!");
                e.printStackTrace();
            }
        }

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
     * 发送消息给其他通道.
     *
     * @param message 消息内容.
     */
    private void sendMessageToOther(String message, SocketChannel myselfChannel) throws Exception {
        // 获取连接了该服务端的所有通道
        for (SelectionKey selectionKey : selector.keys()) {
            Channel channel = selectionKey.channel();
            if (channel instanceof SocketChannel && channel != myselfChannel) {
                SocketChannel target = (SocketChannel) channel;
                target.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.listener();
    }
}
