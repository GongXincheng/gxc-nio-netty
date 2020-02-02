package com.gxc.netty.http.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-01-06 20:07
 */
@Slf4j
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            log.info("msg的类型为：[{}]", msg.getClass().getSimpleName());
            log.info("客户端的地址：[{}]", ctx.channel().remoteAddress());
            log.info("请求的uri：[{}]", httpRequest.uri());
            log.info("请求方法：[{}]\n", httpRequest.method());

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello Client！", CharsetUtil.UTF_8);

            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8")
                    .set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            // 相应消息给浏览器
            // TODO：此时ctx是 [DefaultChannelHandlerContext] 的对象
            ctx.writeAndFlush(httpResponse);
        }
    }

}
