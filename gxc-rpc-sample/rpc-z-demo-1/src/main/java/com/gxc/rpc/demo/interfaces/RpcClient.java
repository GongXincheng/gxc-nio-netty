package com.gxc.rpc.demo.interfaces;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;

/**
 * @author GongXincheng
 * @date 2019-12-10 18:01
 */
public interface RpcClient {

    /**
     * 发起请求，获取响应
     *
     * @param request RpcRequest
     * @return RpcResponse
     */
    RpcResponse sendRequest(RpcRequest request);
}
