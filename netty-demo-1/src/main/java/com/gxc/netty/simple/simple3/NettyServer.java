package com.gxc.netty.simple.simple3;

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
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 服务端.
 *
 * @author GongXincheng
 * @date 2019-12-28 23:18
 */
@Slf4j
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {

        // 创建 BossGroup 和 WorkerGroup 两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 使用 NioServerSocketChannel 作为服务器通道实现
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerChannelHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(9090).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


}

@Slf4j
class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("服务端接收客户端的连接请求，客户端ip：[{}]", ctx.channel().remoteAddress());
        String toClient = "老弟, 你来了！";
        ctx.writeAndFlush(Unpooled.copiedBuffer(toClient, CharsetUtil.UTF_8));
        log.info("Registered事件，响应客户端完成！相应内容：[{}]", toClient);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String message = buf.toString(CharsetUtil.UTF_8);
        log.info("服务端接收客户端发送的内容，内容为：[{}]", message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("服务端相应消息给客户端");

        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(3 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 2", CharsetUtil.UTF_8));
                System.out.println("channel code=" + ctx.channel().hashCode() + ", id = " + ctx.channel().id());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });


        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(5 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵 3", CharsetUtil.UTF_8));
                System.out.println("channel code=" + ctx.channel().hashCode() + ", id = " + ctx.channel().id());
            } catch (Exception ex) {
                System.out.println("发生异常" + ex.getMessage());
            }
        });


        String toClient = "我收到你的消息啦";
        ctx.writeAndFlush(Unpooled.copiedBuffer(toClient, CharsetUtil.UTF_8));
    }
}