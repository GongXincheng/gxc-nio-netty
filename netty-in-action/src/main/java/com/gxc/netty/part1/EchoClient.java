package com.gxc.netty.part1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author GongXincheng
 * @date 2020-01-10 21:32
 */
public class EchoClient {

    private int port;
    private String host;

    private EchoClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public static void main(String[] args) throws Exception {
        EchoClient echoClient = new EchoClient("127.0.0.1", 9090);
        echoClient.start();
    }

    private void start() throws Exception {
        final EchoClientHandler echoClientHandler = new EchoClientHandler();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoClientHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }
}
