package com.gxc.spring.component.server;

import com.alibaba.fastjson.JSONObject;
import com.gxc.spring.component.context.AbstractApplicationContext;
import com.gxc.spring.component.model.RequestDTO;
import com.gxc.spring.component.util.HttpParseUtil;
import com.gxc.spring.model.constant.HttpHeaders;
import com.gxc.spring.model.constant.MediaType;
import com.gxc.spring.model.constant.StringConstant;
import com.gxc.spring.model.constant.WebConfigConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SocketServer 启动
 *
 * @author GongXincheng
 * @date 2019-12-12 15:44
 */
@Slf4j
public class NioServerApplicationContext extends AbstractApplicationContext {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    // 用来处理请求的连接池
    private static final ExecutorService ES = Executors.newCachedThreadPool();

    /**
     * 是否停止
     */
    private volatile boolean shutdown = false;

    /**
     * 构造启动.
     */
    public NioServerApplicationContext(Class<?> config) {
        super(config);
    }

    @Override
    public void start() throws Exception {
        try {
            // 初始化服务端.
            Selector selector = initServer();

            // 监听客户端通道
            listener(selector);

            log.info("|-- Application Server starting Success ！{}", StringConstant.SEPARATOR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 监听客户端通道（异步监听）
     */
    private void listener(Selector selector) {
        ES.execute(new NioServerRequestListener(selector));
    }

    /**
     * 初始化服务端
     */
    private Selector initServer() throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(WebConfigConstant.SERVE_PORT));
        log.info("|-- Server is starting... Port：" + WebConfigConstant.SERVE_PORT);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        return selector;
    }

    @Override
    public void close() throws Exception {
        super.close();
        shutdown = true;
        ES.shutdownNow();
        log.warn("|-- Application Server Closed ！");
    }

    /**
     * 消息通道监听器.
     */
    @AllArgsConstructor
    private class NioServerRequestListener implements Runnable {

        private Selector selector;

        @Override
        public void run() {
            log.info("|-- Start run nio server request listener ...{}", StringConstant.SEPARATOR);

            while (!shutdown) {
                try {
                    selector.select();
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey selectionKey = it.next();
                        it.remove();
                        // 接收就绪
                        if (selectionKey.isAcceptable()) {
                            acceptReadyHandler(selectionKey);
                        }
                        // 读就绪
                        if (selectionKey.isReadable()) {
                            SocketChannel client = (SocketChannel) selectionKey.channel();
                            try {
                                // TODO 读取数据
                                String request = readReadyHandler(client);
                                if (StringUtils.isBlank(request)) {
                                    // 关闭此链接
                                    log.info("|-- Has a connection closed：{}", client.getRemoteAddress());
                                    selectionKey.cancel();
                                } else {
                                    // TODO：解析并处理请求.
                                    Object result = handlerRequest(request);

                                    // TODO：返回数据给客户端
                                    responseMessage(client, result);

                                    // TODO：必须关闭连接，才能将内容响应到HTTP客户端，如果客户端不是HTTP协议，则无需关闭.
                                    client.close();
                                }
                            } catch (Exception e) {
                                log.error("|--> deal client request has error: {}", e.getMessage(), e);
                                client.close();
                                log.warn("|--> Because of exception, close client connection ！");
                            }
                        } //selectionKey.isReadable()
                    } //it.hasNext()
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("|-- stop run nio server request listener ...");
            try {
                selector.close();
                log.info("|-- stop selector success ！...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 解析并处理请求.
         *
         * @param request requestMessage
         * @return result
         */
        private Object handlerRequest(String request) {
            // 解析 Http Body
            RequestDTO parse = HttpParseUtil.parse(request);

            // 匹配合适的处理器 并处理
            System.out.println(JSONObject.toJSONString(parse));

            return JSONObject.toJSONString(parse);
        }

        /**
         * "接收就绪"
         */
        private void acceptReadyHandler(SelectionKey selectionKey) throws Exception {
            SocketChannel client = ((ServerSocketChannel) selectionKey.channel()).accept();
            log.info("|-- Has a Accept ready connection: {}", client.getRemoteAddress());
            client.configureBlocking(false);
            client.register(selectionKey.selector(), SelectionKey.OP_READ);
        }

        /**
         * "读就绪"
         */
        private String readReadyHandler(SocketChannel client) throws Exception {
            StringBuilder sb = new StringBuilder();
            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            while (client.read(buffer) > 0) {
                buffer.flip();
                sb.append(new String(buffer.array(), 0, buffer.limit()));
                buffer.clear();
            }
            return sb.toString();
        }


        /**
         * 相应数据给客户端.
         */
        private void responseMessage(SocketChannel client, Object result) throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 200 OK").append(StringConstant.SEPARATOR_R_N);
            sb.append(HttpHeaders.CONTENT_TYPE + ": " + MediaType.APPLICATION_JSON_UTF8_VALUE).append(StringConstant.SEPARATOR_R_N);
            sb.append(StringConstant.SEPARATOR_R_N);
            sb.append(result);

            log.warn("response body: {}", sb.toString());

            client.write(ByteBuffer.wrap(sb.toString().getBytes()));

            log.info("|-- Request response Success：{}", client.getRemoteAddress());
        }
    }
}
