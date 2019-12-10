package com.gxc.rpc.server.interfaces;

/**
 * RPC的服务端接口
 *
 * @author GongXincheng
 * @date 2019-12-10 16:11
 */
public interface RpcServer {
    /**
     * 启动服务
     */
    void start();

    /**
     * 停止服务
     */
    void stop();
}