package com.gxc.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * TextWebSocketFrame 表示一个文本帧(frame)
 *
 * @author GongXincheng
 * @date 2020-02-02 18:18
 */
@Slf4j
public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.warn("服务器接收到消息：{}", msg.text());

        // 回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now() + "：" + msg.text()));
    }


    /**
     * 当web客户端连接后，触发方法
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // id：表示Channel唯一的值：LongText 是唯一的，ShortText 不是唯一
        log.info("|-> handlerAdded() 被调用，[{}]", ctx.channel().id().asLongText());
        log.info("|-> handlerAdded() 被调用，[{}]", ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("|-> handlerRemoved() 被调用，[{}]", ctx.channel().id().asLongText());
        log.info("|-> handlerRemoved() 被调用，[{}]", ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("|-> 发生异常：{}， 关闭通道", cause.getMessage());
        cause.printStackTrace();
    }
}
