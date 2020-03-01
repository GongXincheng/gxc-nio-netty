package com.gxc.rpc.core.codec.decode;

import com.gxc.rpc.core.protocol.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:24
 */
public class RpcRequestDecoder extends BaseRpcDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RpcRequest request = (RpcRequest) byteArrayToObject(in);
        out.add(request);
    }


}
