package com.rpc.gxc;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import com.rpc.gxc.interfaces.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 客户端调用服务端服务，在代理中调用
 *
 * @author GongXincheng
 * @date 2019-12-11 10:00
 */
public class GxcClient implements RpcClient {

    private final Logger logger = LoggerFactory.getLogger(GxcClient.class);

    private String host;
    private int port;

    public GxcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 发送请求
     */
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket client = new Socket(host, port);
            try {
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());

                out.writeObject(request);

                logger.info("开始调用远程服务：{}:{}, 类名：{}, 方法名：{}", host, port, request.getClassName(), request.getMethodName());
                RpcResponse rpcResponse = (RpcResponse) in.readObject();
                logger.info("服务调用成功！");

                return rpcResponse;
            } catch (Exception e) {
                logger.error("服务端调用出错，message：" + e.getMessage(), e);
                return null;
            }
        } catch (Exception e) {
            logger.error("服务连接失败... message：" + e.getMessage(), e);
            return null;
        }
    }
}
