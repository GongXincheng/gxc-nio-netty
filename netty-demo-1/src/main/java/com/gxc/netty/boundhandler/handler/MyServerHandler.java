package com.gxc.netty.boundhandler.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-02-07 15:58
 */
@Slf4j
public class MyServerHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        log.info("客户端【{}】读取到 Long：【{}】", ctx.channel().remoteAddress(), msg);

        // 给客户端发送Long
        ctx.writeAndFlush(msg + 1L);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出现异常：{}", cause.getMessage(), cause);
    }
}
