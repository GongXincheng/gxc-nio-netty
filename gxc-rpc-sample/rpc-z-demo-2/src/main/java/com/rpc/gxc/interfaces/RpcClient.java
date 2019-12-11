package com.rpc.gxc.interfaces;

import com.gxc.rpc.req.RpcRequest;
import com.gxc.rpc.vo.RpcResponse;

/**
 * @author GongXincheng
 * @date 2019-12-11 10:13
 */
public interface RpcClient {

    RpcResponse sendRequest(RpcRequest request);

}
