package com.gxc.rpc.server;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.server.handler.RequestHandler;
import com.gxc.rpc.server.interfaces.RpcServer;
import com.gxc.rpc.vo.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GongXincheng
 * @date 2019-12-11 13:33
 */
public class NioRpcServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NioRpcServer.class);

    // 用来处理请求的连接池
    private static final ExecutorService es = Executors.newCachedThreadPool();

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    // 默认端口
    private int port = 9000;

    // 是否停止
    private volatile boolean shutdown = false;

    /**
     * 使用默认端口9000，构建一个NIO的RPC服务端
     */
    public NioRpcServer() {
    }

    /**
     * 使用指定端口构建一个BIO的RPC服务端 *
     *
     * @param port 服务端端口
     */
    public NioRpcServer(int port) {
        this.port = port;
    }

    /**
     * 由于 selector.select() 会发生阻塞影响Spring容器启动初始化, 开启新的线程
     */
    @PostConstruct
    public void init() {
        es.execute(this::start);
    }

    @Override
    public void start() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            // 设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            logger.info("|-- ServerSocketChannel 启动成功 ...");

            Selector selector = Selector.open();
            logger.info("|-- Selector 启动成功 ...");
            // 将 ServerSocketChannel 注册到 Selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 启动通道监听
            es.execute(new SelectorChannelListener(selector));
        } catch (Exception e) {
            logger.error("服务端 启动失败..");
        } finally {
            es.shutdown();
        }
    }


    /**
     * 关闭服务.
     */
    @PreDestroy
    @Override
    public void stop() {
        shutdown = true;
    }


    /**
     * 通道监听处理器
     */
    private class SelectorChannelListener implements Runnable {

        private final Logger log = LoggerFactory.getLogger(SelectorChannelListener.class);

        private Selector selector;

        SelectorChannelListener(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    // 阻塞
                    selector.select();
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                    while (it.hasNext()) {
                        SelectionKey selectionKey = it.next();
                        it.remove();

                        // 接收就绪
                        if (selectionKey.isAcceptable()) {
                            SocketChannel clientChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
                            log.info("|-- {} 接收就绪", clientChannel.getRemoteAddress());
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
                        }

                        // 读就绪
                        else if (selectionKey.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                            log.info("|-- {} 读就绪", clientChannel.getRemoteAddress());

                            // 获取 request
                            RpcRequest request = (RpcRequest) getRpcRequest(clientChannel);
                            if (Objects.isNull(request)) {
                                // TODO：必须加上此句，表示客户端关闭！否则会出现客户端无线读就绪循环
                                selectionKey.cancel();
                                break;
                            }

                            // 处理请求
                            RpcResponse response = RequestHandler.handleRequest(request);

                            // 序列化 返回值，并通过Channel返回
                            returnRpcResponse(clientChannel, response);
                        }
                    }
                } catch (Exception e) {
                    log.error("SelectorChannelListener 出现异常: " + e.getMessage(), e);
                }
            }
        }

        /**
         * 将 返回值 返回到客户端
         */
        private void returnRpcResponse(SocketChannel clientChannel, Object object) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            byte[] bytes = baos.toByteArray();
            clientChannel.write(ByteBuffer.wrap(bytes));
            log.info("|-- 返回到客户端完成");
        }

        /**
         * 从 SocketChannel 中获取
         *
         * @param clientChannel 客户端通道
         */
        private Object getRpcRequest(SocketChannel clientChannel) throws IOException, ClassNotFoundException {
            // 获取请求入参
            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
            while (clientChannel.read(buffer) > 0) {
                buffer.flip();
                arrayOut.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            byte[] bytes = arrayOut.toByteArray();
            if (Objects.isNull(bytes) || bytes.length == 0) {
                return null;
            }
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return in.readObject();
        }
    }

}
