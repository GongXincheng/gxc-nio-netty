package com.gxc.netty.part1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author GongXincheng
 * @date 2020-01-10 21:21
 */
public class EchoServer {

    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    /**
     * 启动Netty服务端
     */
    private void start() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        EchoServer echoServer = new EchoServer(9090);
        echoServer.start();
    }

}
