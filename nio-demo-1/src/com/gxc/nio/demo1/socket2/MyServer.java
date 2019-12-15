package com.gxc.nio.demo1.socket2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author GongXincheng
 * @date 2019-12-15 00:44
 */
public class MyServer {

    public void start() throws Exception {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);

        serverChannel.bind(new InetSocketAddress(9000));

        Selector selector = Selector.open();

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()){
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);

                    /////////// 测试 ///////////
                    Set<SelectionKey> keys = selector.keys();
                    for (SelectionKey selectionKey : keys) {
                        SelectableChannel channel = selectionKey.channel();
                        if (channel instanceof ServerSocketChannel) {
                            System.out.println("ServerSocketChannel ！");
                        } else if (channel instanceof SocketChannel) {
                            if (clientChannel == channel) {
                                System.out.println("MySelf ！！！");
                            } else {
                                System.out.println("SocketChannel ！！");
                            }
                        } else {
                            System.out.println("WTF");
                        }
                    }
                    System.out.println("==========================");
                }


                if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    clientChannel.read(buffer);
                    buffer.flip();
                    String message = new String(buffer.array(), 0, buffer.limit());
                    if (message.trim().length() == 0) {
                        System.out.println("close");
                        key.cancel();
                    } else {
                        System.out.println("- " + message);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MyServer server = new MyServer();
        server.start();
    }
}
