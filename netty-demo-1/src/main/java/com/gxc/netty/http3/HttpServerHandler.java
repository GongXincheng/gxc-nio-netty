package com.gxc.netty.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author GongXincheng
 * @date 2020-01-06 22:14
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        // 如果是http请求
        if (msg instanceof DefaultHttpRequest) {
            DefaultHttpRequest httpRequest = (DefaultHttpRequest) msg;

            log.info("请求uri：[{}]", httpRequest.uri());
            log.info("请求方法：[{}]", httpRequest.method().name());
            log.info("客户端地址：[{}]", ctx.channel().remoteAddress());
            log.info("======== 请求头 ========");
            HttpHeaders headers = httpRequest.headers();
            for (Map.Entry<String, String> header : headers) {
                log.info("{} ：{}", header.getKey(), header.getValue());
            }
            log.info("=======================\n");

            // 创建相应客户端内容
            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);

            // 创建http相应对象
            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

            // 设置相应头
            httpResponse.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8")
                    .set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            // 相应到客户端
            ctx.writeAndFlush(httpResponse);

        }

    }
}
