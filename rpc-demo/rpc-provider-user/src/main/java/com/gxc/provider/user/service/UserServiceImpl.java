package com.gxc.provider.user.service;

import com.gxc.rpc.common.entity.UserEntity;
import com.gxc.rpc.common.service.UserService;
import com.gxc.rpc.core.annotation.Service;

import java.time.LocalDateTime;

/**
 * UserServiceImpl.
 *
 * @author GongXincheng
 * @date 2020-03-01 02:52
 */
@Service(value = UserService.class)
public class UserServiceImpl implements UserService {

    @Override
    public String hello(String message) {
        return "RPC -> " + message;
    }

    @Override
    public UserEntity findByCondition(Integer id, String name) {
        return new UserEntity(id, name, LocalDateTime.now());
    }

}
