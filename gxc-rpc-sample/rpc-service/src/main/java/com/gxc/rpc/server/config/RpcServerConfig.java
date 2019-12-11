package com.gxc.rpc.server.config;

import com.gxc.rpc.server.BioRpcServer;
import com.gxc.rpc.server.NioRpcServer;
import com.gxc.rpc.server.registry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author GongXincheng
 * @date 2019-12-10 20:55
 */
@Configuration
@ComponentScan(basePackages = "com.gxc.rpc.server.service.impl")
public class RpcServerConfig {

    /**
     * BIO的RPC服务端
     */
    //@Bean
    public BioRpcServer bioRpcServer() {
        return new BioRpcServer(9000);
    }

    @Bean
    public NioRpcServer nioRpcServer() {
        return new NioRpcServer(9000);
    }

    /**
     * 服务的自动注册器
     */
    @Bean
    public ServiceRegistry serviceRegistry() {
        return new ServiceRegistry();
    }

}
