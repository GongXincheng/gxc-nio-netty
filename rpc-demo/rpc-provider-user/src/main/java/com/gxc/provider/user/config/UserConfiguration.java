package com.gxc.provider.user.config;

import com.gxc.provider.user.netty.NettyServer;
import com.gxc.provider.user.service.UserServiceImpl;
import com.gxc.rpc.common.service.UserService;
import com.gxc.spring.annotation.Bean;
import com.gxc.spring.annotation.Configuration;
import com.gxc.spring.context.ContainerRun;

/**
 * @author GongXincheng
 * @date 2020-03-01 03:48
 */
@Configuration
public class UserConfiguration implements ContainerRun {

    @Bean
    public UserService userServiceImpl() {
        return new UserServiceImpl();
    }

    @Override
    public void start() {
        NettyServer.startServer("127.0.0.1", 8899);
    }
}
