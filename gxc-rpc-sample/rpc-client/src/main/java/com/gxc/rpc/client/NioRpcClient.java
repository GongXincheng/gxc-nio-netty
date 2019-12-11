package com.gxc.rpc.client;

import com.gxc.rpc.client.interfaces.RpcClient;
import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author GongXincheng
 * @date 2019-12-11 14:41
 */
public class NioRpcClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NioRpcClient.class);

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    // 用来处理请求的连接池
    private static final ExecutorService es = Executors.newCachedThreadPool();

    private String host;

    private int port;

    public NioRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) throws Exception {
        SocketChannel clientChannel = null;
        Selector selector = null;
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress(host, port));
            logger.info("|-- 客户端连接成功！");
            clientChannel.configureBlocking(false);
            selector = Selector.open();
            clientChannel.register(selector, SelectionKey.OP_READ);
            logger.info("|-- 客户端Channel注册Selector成功！");

            // 监听并获取返回
            Future<RpcResponse> submit = es.submit(new ResponseHandlerListener(selector));

            // 将入参序列化
            ByteArrayOutputStream outArray = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outArray);
            out.writeObject(request);
            byte[] bytes = outArray.toByteArray();
            // 发送请求入参
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            clientChannel.write(buffer);
            buffer.clear();

            return submit.get();
        } catch (Exception e) {
            logger.error("客户端连接失败：{}", e.getMessage());
            throw new RuntimeException("客户端连接失败");
        } finally {
            if (selector != null) {
                selector.close();
            }
            if (clientChannel != null) {
                clientChannel.close();
            }
            es.shutdown();
        }
    }

    /**
     * 客户端 通道监听
     */
    private static class ResponseHandlerListener implements Callable<RpcResponse> {

        private static final Logger log = LoggerFactory.getLogger(ResponseHandlerListener.class);

        private Selector selector;

        ResponseHandlerListener(Selector selector) {
            this.selector = selector;
        }

        @Override
        public RpcResponse call() throws Exception {
            // 阻塞
            selector.select();
            try {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    it.remove();
                    if (selectionKey.isReadable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        log.info("|-- {} 客户端'读就绪'", client);

                        // 获取返回值
                        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
                        ByteArrayOutputStream outArray = new ByteArrayOutputStream();
                        while (client.read(buffer) > 0) {
                            buffer.flip();
                            outArray.write(buffer.array(), 0, buffer.limit());
                            buffer.clear();
                        }
                        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outArray.toByteArray()));
                        return (RpcResponse) in.readObject();
                    }
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
