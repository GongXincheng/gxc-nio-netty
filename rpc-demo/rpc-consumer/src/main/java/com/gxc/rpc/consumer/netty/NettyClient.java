package com.gxc.rpc.consumer.netty;

import com.gxc.rpc.core.codec.decode.RpcResponseDecoder;
import com.gxc.rpc.core.codec.encode.RpcRequestEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:41
 */
@Slf4j
public class NettyClient {

    public static void startClient(String host, int port) {
        initClient(host, port);
    }

    private static void initClient(String host, int port) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcRequestEncoder());
                        pipeline.addLast(new RpcResponseDecoder());
                        pipeline.addLast(NettyClientHandler.getInstance());
                    }
                });

        try {
            bootstrap.connect().sync();
        } catch (Exception e) {
            log.error("Netty client connection server failed ! [{}]", e.getMessage(), e);
            eventLoopGroup.shutdownGracefully();
        }
    }


}
