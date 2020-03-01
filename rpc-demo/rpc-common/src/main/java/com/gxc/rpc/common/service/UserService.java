package com.gxc.rpc.common.service;

import com.gxc.rpc.common.entity.UserEntity;

/**
 * UserService
 *
 * @author GongXincheng
 * @date 2020-03-01 02:45
 */
public interface UserService {

    String hello(String message);

    UserEntity findByCondition(Integer id, String name);

}
