package com.gxc.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO：Http 处理器
 * 1：SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
 * 2：HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject 类型
 *
 * @author GongXincheng
 * @date 2020-01-05 21:13
 */
@Slf4j
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 读取客户端的数据.
     *
     * @param ctx ChannelHandlerContext
     * @param msg HttpObject
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断 msg 是不是 HttpRequest 请求
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            log.info("msg的类型为：[{}]", msg.getClass().getSimpleName());
            log.info("客户端的地址：[{}]", ctx.channel().remoteAddress());
            log.info("请求的uri：[{}]", httpRequest.uri());
            log.info("请求方法：[{}]\n", httpRequest.method());

            // 获取信息给浏览器，[HTTP协议]
            ByteBuf content = Unpooled.copiedBuffer("Hello, 我是Netty服务器", CharsetUtil.UTF_8);

            // 构造一个http相应，即 HttpResponse
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            // 设置相应头
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8")
                    .set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 将构建好的 response 返回
            // TODO：此时ctx是 [DefaultChannelHandlerContext] 的对象
            ctx.writeAndFlush(httpResponse);
        }
    }
}
