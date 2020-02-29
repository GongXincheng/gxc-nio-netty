package com.gxc.netty.handlerasync;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义Handler 异步处理业务逻辑Demo.
 * <p>
 * 方式1：handler中加入线程池
 * 方式2：Context中添加线程池
 *
 * @author GongXincheng
 * @date 2020-02-29 17:24
 */
@Slf4j
public class MyServer {

    /**
     * 方式2：Context中添加线程池
     */
    private static final EventExecutorGroup GROUP = new DefaultEventExecutorGroup(2);

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        final MyServerHandler myServerHandler = new MyServerHandler();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //pipeline.addLast(myServerHandler);

                            // TODO：如果在addLast时，有指定的[EventExecutorGroup] 那么该handler会优先加入到该线程池中
                            pipeline.addLast(GROUP, myServerHandler);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(9999).sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Server start success！");
                } else {
                    log.error("Server start failed !", future.cause());
                }
            });

            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

}

@Slf4j
@ChannelHandler.Sharable
class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 方式1：handler中加入线程池，充当业务线程池，可以将任务提交到该线程池中，创建了16个线程
     */
    static final EventExecutorGroup GROUP = new DefaultEventExecutorGroup(16);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("|-> Into {}'s channelRead()", this.getClass().getSimpleName());

        /* 测试： 将任务放入 NioEventLoop 的任务队列中，会和 Handler 处理事件的线程使用同一个，会发送阻塞
        ctx.executor().execute(() -> {
            log.info("|-> NioEventLoop's task");
            try {
                Thread.sleep(1000L);
                //返回消息给客户端.
                ctx.writeAndFlush(Unpooled.copiedBuffer("Good morning netty client!", CharsetUtil.UTF_8));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }); */


        /* TODO：将任务提交到 GROUP 线程池
        GROUP.submit(() -> {
            // 接收客户端消息
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(buffer);
            String message = new String(buffer, CharsetUtil.UTF_8);

            // 休眠
            Thread.sleep(10 * 1000);
            log.info("group.submit message：[{}]", message);

            //返回消息给客户端.
            ctx.writeAndFlush(Unpooled.copiedBuffer("group.submit: Hello ！", CharsetUtil.UTF_8));
            return null;
        }); */


        // 接收客户端消息
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buffer);
        String message = new String(buffer, CharsetUtil.UTF_8);

        // 休眠
        Thread.sleep(10 * 1000);
        log.info("得到客户端消息message：[{}]", message);

        //返回消息给客户端.
        ctx.writeAndFlush(Unpooled.copiedBuffer("group: Hello ！", CharsetUtil.UTF_8));
        //返回消息给客户端.
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello netty client!", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Handler has error, message：{}", cause.getMessage(), cause);
        ctx.channel().close();
    }
}
