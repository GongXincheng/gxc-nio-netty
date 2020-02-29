package com.gxc.netty.rpc.netty;

import com.gxc.netty.rpc.provider.service.HelloServiceImpl;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-02-29 22:32
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // 获取客户端发送的消息 并调用服务
        log.info("Received client's message：[{}]", msg);

        // 客户端在调用服务器的api时，需要定义一个协议.
        // 要求：每次发消息时都必须以某个字符串开头 "HelloService#hello#参数"
        if (msg.startsWith(Constant.PROTOCOL_PREFIX)) {
            String param = msg.substring(Constant.PROTOCOL_PREFIX.lastIndexOf("#") + 1);
            String result = new HelloServiceImpl().hello(param);
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
