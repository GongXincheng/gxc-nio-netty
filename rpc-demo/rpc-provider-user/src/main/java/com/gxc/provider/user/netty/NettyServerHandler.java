package com.gxc.provider.user.netty;

import com.gxc.provider.user.handler.RequestHandler;
import com.gxc.rpc.core.protocol.RpcRequest;
import com.gxc.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:12
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.warn("服务端连接：" + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        log.info("Server接收到请求：{}", request);
        RpcResponse<Object> response = RequestHandler.process(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
