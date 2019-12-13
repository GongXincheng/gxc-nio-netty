package com.gxc.spring.controller;

import com.google.common.collect.Maps;
import com.gxc.spring.annotation.Controller;
import com.gxc.spring.annotation.PathVariable;
import com.gxc.spring.annotation.RequestMapping;
import com.gxc.spring.annotation.Resource;
import com.gxc.spring.model.entity.User;
import com.gxc.spring.model.enums.RequestMethod;
import com.gxc.spring.service.UserService;

import java.util.HashMap;

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

    @RequestMapping(value = "/user/admin/{id}", method = RequestMethod.GET)
    public HashMap<Object, Object>  findById2(@PathVariable Integer id) {
        User byUserId = userService.findByUserId(id);
        HashMap<Object, Object> objectObjectHashMap = Maps.newHashMap();
        objectObjectHashMap.put("admin", byUserId);
        return objectObjectHashMap;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User findById() {
        return new User(1, "aaa", 1, "admin", "asd");
    }

}
