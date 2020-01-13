package com.gxc.netty.http3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2020-01-06 22:06
 */
@Slf4j
public class HttpServer {

    private int port;

    public HttpServer(int port) {
        this.port = port;
    }

    private void start() {

        // 创建线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 启动服务的辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("MyHttpServerCodec", new HttpServerCodec());
                            ch.pipeline().addLast("MyHttpServerHandler", new HttpServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("服务启动成功！[{}]", future.channel().localAddress());
                    } else {
                        log.error("服务启动失败！");
                    }
                }
            });

            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        HttpServer httpServer = new HttpServer(8080);
        httpServer.start();

    }

}
