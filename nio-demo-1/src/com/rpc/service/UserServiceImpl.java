package com.rpc.service;

import com.rpc.req.RpcRequest;
import com.rpc.req.RpcResponse;

import java.io.Serializable;

/**
 * @author GongXincheng
 * @date 2019-12-09 21:17
 */
public class UserServiceImpl implements UserService, Serializable {

    @Override
    public RpcResponse login(RpcRequest rpcRequest) {
        System.out.println(UserServiceImpl.class.getSimpleName() + "：" + rpcRequest);
        System.out.println("username：" + rpcRequest);
        return new RpcResponse(100, "gongxincheng", rpcRequest);
    }

    @Override
    public RpcResponse login(String username, String password) {
        System.out.println(UserServiceImpl.class.getSimpleName() + "....");
        System.out.println("username：" + username);
        System.out.println("password：" + password);
        return new RpcResponse(100, "gongxincheng", new RpcRequest());
    }

}
