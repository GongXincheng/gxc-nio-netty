package com.gxc.spring.service;

import com.gxc.spring.model.entity.User;

/**
 * @author GongXincheng
 * @date 2019-12-12 10:08
 */
public interface UserService {

    User findByUserId(Integer id);

}
