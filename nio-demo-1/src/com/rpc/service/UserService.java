package com.rpc.service;

import com.rpc.req.RpcRequest;
import com.rpc.req.RpcResponse;

/**
 * @author GongXincheng
 * @date 2019-12-09 18:04
 */
public interface UserService {

    RpcResponse login(RpcRequest request);

    RpcResponse login(String username, String password);
}
