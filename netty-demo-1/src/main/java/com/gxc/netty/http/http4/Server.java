package com.gxc.netty.http.http4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author GongXincheng
 * @date 2020-01-08 22:58
 */
@Slf4j
public class Server {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ServerHandler());
                        }
                    });

//            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("服务启动成功！{}", future.channel().localAddress());
                    } else {
                        log.error("服务启动失败");
                    }
                }
            });


            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    static class ServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            System.out.println(channel.hashCode() + "____" + channel);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof DefaultHttpRequest) {
                DefaultHttpRequest httpRequest = (DefaultHttpRequest) msg;

                log.info("客户端地址：[{}]", ctx.channel().remoteAddress());
                log.info("请求方法：[{}]", httpRequest.method().name());
                log.info("请求uri：[{}]", httpRequest.uri());
                log.info("======== 请求头 ========");
                HttpHeaders headers = httpRequest.headers();
                for (Map.Entry<String, String> header : headers) {
                    log.info("{} ：{}", header.getKey(), header.getValue());
                }
                log.info("=======================\n");

                ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World!", CharsetUtil.UTF_8);

                HttpResponse httpResponse = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);

                httpResponse.headers()
                        .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        .set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

                ctx.writeAndFlush(httpResponse);
            }
        }
    }
}
