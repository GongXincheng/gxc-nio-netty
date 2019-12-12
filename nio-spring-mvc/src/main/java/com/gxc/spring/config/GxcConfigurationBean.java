package com.gxc.spring.config;

import com.gxc.spring.annotation.Bean;
import com.gxc.spring.annotation.Configuration;
import com.gxc.spring.controller.UserController;
import com.gxc.spring.service.UserService;
import com.gxc.spring.service.impl.UserServiceImpl;

/**
 * @author GongXincheng
 * @date 2019-12-12 13:59
 */
@Configuration
public class GxcConfigurationBean {

    @Bean
    public UserController userController() {
        return new UserController();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

}
