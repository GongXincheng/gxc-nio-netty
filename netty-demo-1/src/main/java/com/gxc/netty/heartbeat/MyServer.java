package com.gxc.netty.heartbeat;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Netty 心跳机制.
 *
 * @author GongXincheng
 * @date 2020-02-01 15:19
 */
@Slf4j
public class MyServer {

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(9090))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 在 bossGroup 中添加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // TODO 加入一个 Netty 提供 IdleStateHandler
                            //  IdleStateHandler 是 Netty 提供的处理空闲状态的处理器
                            //      long readerIdleTime：表示多长时间没有【读】(Server没有读Client的数据)，就会发送一个心跳检测包检测是否连接
                            //      long writerIdleTime：表示多长时间没有【写】就会发送一个心跳检测包检测是否连接
                            //      long allIdleTime：表示多长时间既没有【读】也没有【写】就会发送一个心跳检测包检测是否连接
                            //  -
                            //  -> 文档说明：
                            //          Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                            //  -
                            //  说明：当 [IdleStateEvent] 被触发后，就会传递给管道的下一个Handler去处理，通过调用(触发)下一个 Handler 的【userEventTriggered】方法处理 IdleStateEvent 事件
                            //       读空闲 / 写空闲 / 读写空闲
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));

                            // TODO：加入一个对空闲检测进一步处理的 Handler (自定义)
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Server start Success ！");
                } else {
                    log.error("服务启动失败");
                }
            });

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }


    }

}
