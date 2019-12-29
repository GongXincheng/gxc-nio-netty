package com.gxc.netty.simple3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author GongXincheng
 * @date 2019-12-29 00:04
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientChannelHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9090).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }

}

@Slf4j
class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive事件发生！服务端ip：[{}]", ctx.channel().remoteAddress());
        String toServer = "Hello Netty Server！";
        ctx.writeAndFlush(Unpooled.copiedBuffer(toServer, CharsetUtil.UTF_8));
        log.info("客户端发送消息完成：消息内容：[{}]", toServer);


//        ctx.channel().eventLoop().execute(() -> {
//            Scanner sc = new Scanner(System.in);
//            while (sc.hasNext()) {
//                String message = sc.next();
//                ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
//                log.info("客户端发送消息：[{}]", message);
//            }
//        });

        // OK
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                String message = sc.next();
                ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                log.info("客户端发送消息：[{}]", message);
            }
        }).start();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String message = buf.toString(CharsetUtil.UTF_8);
        log.info("客户端接收客户端发送的内容，内容为：[{}]", message);
    }
}