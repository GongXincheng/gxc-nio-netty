package com.gxc.netty.rpc.provider.bootstrap;

import com.gxc.netty.rpc.netty.NettyServer;

/**
 * 启动服务提供者：NettyServer
 *
 * @author GongXincheng
 * @date 2020-02-29 22:14
 */
public class ServerBootstrap {

    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1", 7000);
    }

}
