package com.gxc.spring.controller;

import com.gxc.spring.annotation.Controller;
import com.gxc.spring.annotation.RequestMapping;
import com.gxc.spring.annotation.Resource;
import com.gxc.spring.annotation.PathVariable;
import com.gxc.spring.model.entity.User;
import com.gxc.spring.model.enums.RequestMethod;
import com.gxc.spring.service.UserService;

/**
 * @author GongXincheng
 * @date 2019-12-12 10:04
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable Integer id) {
        return userService.findByUserId(id);
    }

}
