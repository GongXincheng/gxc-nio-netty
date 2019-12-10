package com.gxc.service;

import com.gxc.req.RpcRequest;
import com.gxc.req.RpcResponse;

/**
 * @author GongXincheng
 * @date 2019-12-09 18:04
 */
public interface UserService {

    RpcResponse login(RpcRequest request);

}
