package com.gxc.netty.groupchat.groupchat1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 群聊服务端.
 *
 * @author GongXincheng
 * @date 2020-02-03 10:03
 */
@Slf4j
public class ChatServer {

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            final ChatServerHandler serverHandler = new ChatServerHandler();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(9090))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加 Netty 空闲状态处理器
                            pipeline.addLast(new IdleStateHandler(20, 20, 20, TimeUnit.SECONDS));
                            pipeline.addLast(new MyIdleStateHandler());

                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                            pipeline.addLast(serverHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("群聊系统Server启动成功！");
                } else {
                    log.error("群聊系统服务端启动失败！");
                }
            });

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}

/**
 * 服务端处理器.
 */
@Slf4j
@ChannelHandler.Sharable
class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("【handlerAdded】执行了，{}", channel.remoteAddress());

        // 通知其他客户端
        CHANNEL_GROUP.writeAndFlush("客户端【" + channel.remoteAddress() + "】上线！");
        CHANNEL_GROUP.add(channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.warn("上线【channelActive】，客户端【{}】上线了", ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("【handlerRemoved】执行了，{}", ctx.channel().remoteAddress());
        CHANNEL_GROUP.writeAndFlush("客户端【" + ctx.channel().remoteAddress() + "】离开了聊天室！");

        // 无须手动移除
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("离线了【channelInactive】，客户端【{}】离线了", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        // 转发给其他客户端，除了自己
        for (Channel clientChannel : CHANNEL_GROUP) {
            if (clientChannel != channel) {
                String message = FORMAT.format(LocalDateTime.now()) + " " + channel.remoteAddress() + " 说：\n" + msg + "\n";
                clientChannel.writeAndFlush(message);
            } else {
                clientChannel.writeAndFlush("自己：" + msg + "\n");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常, 信息：{}, 通道：[{}]", cause.getMessage(), ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}




/**
 * 空闲状态处理器 事件处理.
 */
@Slf4j
class MyIdleStateHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.ALL_IDLE) {
                log.error("读写空闲 20 秒！关闭通道：[{}]", ctx.channel().remoteAddress());
                ctx.writeAndFlush(Unpooled.copiedBuffer("20秒无响应，退出", CharsetUtil.UTF_8));
                ctx.close();
            }
        }
    }
}