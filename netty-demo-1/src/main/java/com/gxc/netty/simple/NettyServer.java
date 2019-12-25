package com.gxc.netty.simple;

import com.gxc.netty.simple.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author GongXincheng
 * @date 2019-12-22 20:22
 */
public class NettyServer {

    public static void main(String[] args) throws Exception {

        // 创建BossGroup 和 WorkerGroup
        // 1：创建了两个线程组 BossGroup 和 WorkerGroup
        // 2：BossGroup：只是处理连接请求。WorkerGroup：真的与客户端业务处理
        // 3：两个都是无限循环

        // bossGroup 和 workerGroup 含有的子线程(NIOEventLoop)的个数
        //    默认CPU的核心数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用链式编程来进行设置, // 设置两个线程组
            bootstrap.group(bossGroup, workerGroup)
                    // 使用 NioServerSocketChannel 作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 给 WorkerGroup 的 EventLoop 对应的管道设置处理器
                    // 创建一个通道初始化对象（匿名对象）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 给 PipeLine 设置处理器
                         */
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 想管道的最后增加一个处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 启动服务器并且绑定端口号，并且同步，生成一个 ChannelFuture 对象
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            System.out.println("|-- Server is ready！ --|");

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭 bossGroup 和 workerGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
