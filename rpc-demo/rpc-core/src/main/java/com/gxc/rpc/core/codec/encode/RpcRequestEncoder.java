package com.gxc.rpc.core.codec.encode;

import com.gxc.rpc.core.protocol.RpcRequest;
import io.netty.channel.ChannelHandler;

/**
 * 请求编码器
 *
 * @author GongXincheng
 * @date 2020-03-01 21:20
 */
@ChannelHandler.Sharable
public class RpcRequestEncoder extends BaseRpcEncoder<RpcRequest> {

}
