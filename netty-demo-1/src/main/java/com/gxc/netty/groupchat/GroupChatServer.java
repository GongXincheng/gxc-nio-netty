package com.gxc.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author GongXincheng
 * @date 2020-01-13 08:47
 */
@Slf4j
public class GroupChatServer {

    /**
     * 端口号
     */
    private int port;

    private GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * run方法处理客户请求
     */
    private void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            // 自定义ChannelHandler
            final GroupChatServerHandler groupChatServerHandler = new GroupChatServerHandler();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress("127.0.0.1", port))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 获取 pipeLine
                            ChannelPipeline pipeline = ch.pipeline();
                            // 向 pipeLine 中加入[解码器]
                            pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                            // 向 pipeLine 中加入[编码器]
                            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                            // 加入自己的业务处理Handler
                            pipeline.addLast(groupChatServerHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("聊天室服务端启动：{}", future.channel().localAddress());
                } else {
                    log.error("聊天室服务端启动失败：{}", future.cause().getMessage(), future.cause());
                }
            });

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws Exception {
        GroupChatServer server = new GroupChatServer(9090);
        server.run();
    }


}
