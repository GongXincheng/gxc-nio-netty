package com.gxc.rpc.client;

import com.gxc.rpc.client.interfaces.RpcClient;
import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author GongXincheng
 * @date 2019-12-10 17:05
 */
public class BioRpcClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(BioRpcClient.class);

    private String host;

    private int port;

    public BioRpcClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            Socket client = new Socket(host, port);

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            logger.info("建立连接成功:{}:{}", host, port);

            out.writeObject(request);

            logger.info("发起请求,目标主机{}:{}，服务:{}.{}({})", host, port, request.getClassName(),
                    request.getMethodName(), StringUtils.join(request.getParameterTypes(),","));

            // 获取结果
            return (RpcResponse) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("客户端-服务调用失败");
        }
    }

}
