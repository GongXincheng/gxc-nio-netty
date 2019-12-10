package com.gxc.rpc.demo;

import com.gxc.rpc.demo.handler.RequestHandler;
import com.gxc.rpc.demo.interfaces.RpcServer;
import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author GongXincheng
 * @date 2019-12-10 18:04
 */
public class BioRpcServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(BioRpcServer.class);

    /**
     * 线程池
     */
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * 服务端口号
     */
    private int port = 9000;

    public BioRpcServer(int port) {
        this.port = port;
    }

    /**
     * 是否停止
     */
    private volatile boolean shutdown = false;

    @Override
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (!shutdown) {
                Socket client = serverSocket.accept();
                threadPool.execute(() -> {
                    try {
                        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

                        RpcRequest request = (RpcRequest) in.readObject();
                        logger.info("接收请求，{}.{}({})", request.getClassName(),
                                request.getMethodName(), StringUtils.join(request.getParameterTypes(), ", "));
                        logger.info("请求参数:{}", StringUtils.join(request.getParameters(), ", "));

                        RpcResponse result = RequestHandler.handlerRequest(request);
                        out.writeObject(result);
                    } catch (Exception e) {
                        logger.error("客户端连接异常，客户端{}", client.getInetAddress().toString());
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        shutdown = true;
        logger.info("服务即将停止");
    }
}
