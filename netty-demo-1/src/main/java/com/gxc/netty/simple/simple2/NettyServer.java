package com.gxc.netty.simple.simple2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @author GongXincheng
 * @date 2019-12-24 23:37
 */
public class NettyServer {

    public static void main(String[] args) throws Exception {

        /**
         * 创建 bossGroup 和 workerGroup 两个线程组
         *
         * 1：创建两个线程组 bossGroup 和 workerGroup
         * 2：bossGroup 只是处理连接请求，真正的和客户端业务处理，会交给workerGroup完成
         * 3：两个都是无限循环
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {


            // 创建服务端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 设置两个线程组
            bootstrap.group(bossGroup, workerGroup)
                    // 使用 NioServerSocketChannel 作为服务器通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到的连接数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    // 创建一个通道测试对象（匿名对象）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 给我们的 workerGroup 的 NioEventLoop 对应的管道设置处理器
                         */
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 绑定端口并同步，生成了一个 ChannelFuture 对象
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();
            System.out.println("|--> Server is ready .... <--|");

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

/**
 * 说明：
 * 1：我们定义一个 handler 需要继承 netty 规定好的某个 HandlerAdapter
 * 2：这是我们自定义的 handler 才能成为一个 Handler
 */
class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(System.currentTimeMillis() + " channelRegistered：" + ctx.channel().remoteAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer("你来了？弟弟？", CharsetUtil.UTF_8));
    }

    /**
     * 读取实际数据（可以读取客户端发送的消息）
     * 1：ChannelHandlerContext 上下文对象：含有 管道(PipeLine)、通道(Channel)、连接地址
     * 2：Object msg：就是客户端发送的数据 默认是 Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("|-- Server ChannelHandlerContext is：" + ctx);

        // 将 msg 转成 ByteBuf
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("|--> 客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("|--> 客户端地址为：" + ctx.channel().remoteAddress());

    }

    /**
     * 数据读取完毕
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 对发送的数据进行编码
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello Client!", CharsetUtil.UTF_8);

        // 将数据写入到缓冲并刷新
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 需要关闭通道
        ctx.channel().close();
    }
}
