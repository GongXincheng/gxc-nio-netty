package com.gxc.spring.service.impl;

import com.gxc.spring.model.entity.User;
import org.junit.Test;

/**
 * @author GongXincheng
 * @date 2019-12-12 13:38
 */
public class UserServiceImplTest {

    @Test
    public void findByUserId() {

        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.findByUserId(10);
        System.out.println(user);

    }
}