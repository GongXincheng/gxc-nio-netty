package com.rpc.gxc;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import com.rpc.gxc.handler.RequestHandler;
import com.rpc.gxc.interfaces.RpcServer;
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
 * @author GongXincheng
 * @date 2019-12-11 10:00
 */
public class GxcServer implements RpcServer {

    private final Logger logger = LoggerFactory.getLogger(GxcServer.class);

    private ExecutorService pool = Executors.newCachedThreadPool();

    private boolean hasStop = false;

    private int port;

    public GxcServer(int port) {
        this.port = port;
    }

    @PostConstruct
    public void init() {
        pool.submit(this::start);
    }

    @Override
    public void start() {

        try {
            ServerSocket server = new ServerSocket(port);

            while (!hasStop) {
                Socket client = server.accept();

                pool.execute(() -> {
                    try {
                        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

                        RpcRequest request = (RpcRequest)in.readObject();
                        RpcResponse result = RequestHandler.handler(request);

                        out.writeObject(result);
                    } catch (Exception e) {
                        logger.error("服务端service执行异常：" + e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("服务端启动失败：" + e.getMessage(), e);
        }
    }

    @PreDestroy
    @Override
    public void stop() {
        hasStop = true;
    }


}
