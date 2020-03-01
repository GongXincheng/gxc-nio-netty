package com.gxc.rpc.core.codec.decode;

import com.gxc.rpc.core.protocol.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:24
 */
public class RpcResponseDecoder extends BaseRpcDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RpcResponse response = (RpcResponse) byteArrayToObject(in);
        out.add(response);
    }
}
