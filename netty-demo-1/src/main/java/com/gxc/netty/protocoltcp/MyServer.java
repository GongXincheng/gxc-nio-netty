package com.gxc.netty.protocoltcp;

import com.gxc.netty.protocoltcp.decoder.MyMessageDecoder;
import com.gxc.netty.protocoltcp.encoder.MyMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * 粘包拆包测试.
 *
 * @author GongXincheng
 * @date 2020-02-07 15:43
 */
@Slf4j
public class MyServer {

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .localAddress(new InetSocketAddress(9090))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 解码器
                            pipeline.addLast(new MyMessageDecoder());
                            pipeline.addLast(new MyMessageEncoder());

                            pipeline.addLast(new MyServerHandler2());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}

@Slf4j
//@ChannelHandler.Sharable
class MyServerHandler2 extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//        byte[] buffer = new byte[msg.readableBytes()];
//        msg.readBytes(buffer);
//
//        // 将buffer转成字符串
//        String message = new String(buffer, CharsetUtil.UTF_8);
//        System.out.println("服务器接收到数据：" + message);
//        log.warn("服务端接收到数据量 = [{}]", ++count);
//
//        // 服务器回送数据给客户端：回送随机id值
//        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID().toString(), CharsetUtil.UTF_8);
//        ctx.writeAndFlush(responseByteBuf);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        // 接收到数据并处理
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("服务端接收数据：长度：" + len + "，内容：" + new String(content, CharsetUtil.UTF_8));
        log.warn("服务端接收到数据量 = [{}]", ++count);


        // TODO：回复消息
        byte[] responseContent = UUID.randomUUID().toString().getBytes(CharsetUtil.UTF_8);
        int length = responseContent.length;
        MessageProtocol response = MessageProtocol.builder()
                .len(length)
                .content(responseContent).build();
        ctx.writeAndFlush(response);
    }
}