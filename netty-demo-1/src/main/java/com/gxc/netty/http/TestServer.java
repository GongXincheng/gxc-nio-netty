package com.gxc.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-01-05 21:13
 */
@Slf4j
public class TestServer {

    public static void main(String[] args) {

        EventLoopGroup eventLoopGroupBoss = new NioEventLoopGroup();
        EventLoopGroup eventLoopGroupWorker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(eventLoopGroupBoss, eventLoopGroupWorker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 向管道中加入处理器
                            // 得到管道
                            ChannelPipeline pipeline = ch.pipeline();

                            // 加入一个 Netty 提供的 httpServerCodec（codec => [coder - decoder]）编解码器
                            // TODO：HttpServerCodec 说明：HttpServerCodec 是Netty提供处理http的编解码器
                            pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());

                            // 添加一个自定义的处理器
                            pipeline.addLast("TestHttpServerHandler", new TestHttpServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("服务启动成功！host：{}", future.channel().localAddress());
                }
            });

            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            eventLoopGroupBoss.shutdownGracefully();
            eventLoopGroupWorker.shutdownGracefully();
        }

    }

}
