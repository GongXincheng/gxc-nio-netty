package com.gxc.rpc.service;

import it.chusen.socket.biorpc.BioRpcResponse;

/**
 * @author chusen
 * @date 2019/12/9 17:04
 */
public class UserService {
    public BioRpcResponse login(String userName, String password) {
        System.out.println("UserService -- login" + userName + ":" + password);
        if ("chusen".equals(userName) && "123".equals(password)) {
            return BioRpcResponse.ok("登录成功！");
        }
        return BioRpcResponse.fail("用户名或密码错误！");
    }
}