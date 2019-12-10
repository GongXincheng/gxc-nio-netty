package com.gxc.rpc.server;

import com.gxc.rpc.server.handler.RequestHandler;
import com.gxc.rpc.server.interfaces.RpcServer;
import com.gxc.rpc.req.RpcRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端（使用BIO的方式）
 *
 * @author GongXincheng
 * @date 2019-12-10 16:38
 */
public class BioRpcServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(BioRpcServer.class);

    // 用来处理请求的连接池
    private static final ExecutorService es = Executors.newCachedThreadPool();

    // 默认端口
    private int port = 9000;

    // 是否停止
    private volatile boolean shutdown = false;

    /**
     * 使用默认端口9000，构建一个BIO的RPC服务端
     */
    public BioRpcServer() {
    }

    /**
     * 使用指定端口构建一个BIO的RPC服务端 *
     *
     * @param port 服务端端口
     */
    public BioRpcServer(int port) {
        this.port = port;
    }

    /**
     * 启动Socket服务
     */
    @PostConstruct
    public void init() {
        es.submit(this::start);
    }

    @Override
    public void start() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            logger.info("服务启动成功，端口:{}", this.port);

            while (!this.shutdown) {
                // 接收客户端请求
                Socket client = server.accept();

                // 线程池的线程执行请求
                es.execute(() -> {
                    try {
                        client.getInputStream();

                        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

                        RpcRequest request = (RpcRequest) in.readObject();
                        logger.info("接收请求，{}.{}({})", request.getClassName(),
                                request.getMethodName(), StringUtils.join(request.getParameterTypes(), ", "));
                        logger.info("请求参数:{}", StringUtils.join(request.getParameters(), ", "));

                        // 处理请求
                        out.writeObject(RequestHandler.handleRequest(request));
                    } catch (Exception e) {
                        logger.error("客户端连接异常，客户端{}", client.getInetAddress().toString());
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("服务启动失败", e);
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        this.shutdown = true;
        logger.info("服务即将停止");
    }

}
