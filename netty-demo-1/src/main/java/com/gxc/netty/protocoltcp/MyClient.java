package com.gxc.netty.protocoltcp;

import com.gxc.netty.protocoltcp.decoder.MyMessageDecoder;
import com.gxc.netty.protocoltcp.encoder.MyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 粘包拆包测试.
 *
 * @author GongXincheng
 * @date 2020-02-07 15:43
 */
public class MyClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 9090))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 编码器
                            pipeline.addLast(new MyMessageEncoder());

                            // 解码器
                            pipeline.addLast(new MyMessageDecoder());

                            pipeline.addLast(new MyClientHandler2());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}

@Slf4j
class MyClientHandler2 extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端发送【10】条数据, '宫新程 加油！'
        for (int i = 0; i < 5; i++) {
            String message = "宫新程 加油！" + i;
            byte[] bytes = message.getBytes(CharsetUtil.UTF_8);

            // 创建协议包
            MessageProtocol messageProtocol = MessageProtocol.builder()
                    .len(bytes.length)
                    .content(bytes).build();

            ctx.writeAndFlush(messageProtocol);
        }
    }

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

        System.out.println("客户端接收数据：长度：" + len + "，内容：" + new String(content, CharsetUtil.UTF_8));
        log.warn("服务端接收到数据量 = [{}]", ++count);
    }
}