package com.gxc.rpc.consumer.controller;

import com.gxc.rpc.common.entity.UserEntity;
import com.gxc.rpc.common.service.UserService;
import com.gxc.rpc.core.annotation.Reference;
import com.gxc.spring.annotation.Component;

/**
 * @author GongXincheng
 * @date 2020-03-01 15:49
 */
@Component
public class UserController {

    @Reference
    private UserService userService;

    public String hello(String message) {
        return userService.hello(message);
    }

    public UserEntity findByCondition(Integer id, String name) {
        return userService.findByCondition(id, name);
    }
}
