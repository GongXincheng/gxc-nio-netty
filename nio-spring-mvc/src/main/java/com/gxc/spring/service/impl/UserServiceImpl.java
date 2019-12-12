package com.gxc.spring.service.impl;

import com.gxc.spring.annotation.Service;
import com.gxc.spring.model.entity.User;
import com.gxc.spring.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author GongXincheng
 * @date 2019-12-12 10:08
 */
@Service
public class UserServiceImpl implements UserService {

    private final static Integer INIT_LENGTH = 10;
    private final static List<User> INIT_USER_LIST = new ArrayList<>(10);

    /*
     * 初始化数据
     */
    static {
        for (int i = 1; i <= INIT_LENGTH; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername("name_" + i);
            user.setWeChet("weixin_" + i);
            user.setAge(i * 2);
            user.setEmail("email_" + i);
            INIT_USER_LIST.add(user);
        }
    }

    @Override
    public User findByUserId(Integer id) {
        return INIT_USER_LIST.parallelStream().filter(e -> Objects.equals(e.getId(), id)).findFirst().orElse(null);
    }


}
