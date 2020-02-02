package com.gxc.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author GongXincheng
 * @date 2020-01-29 23:25
 */
@Slf4j
public class GroupChatClient {

    private static final String CLOSE_STR = "886";
    private final String host;
    private final int port;

    private GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void run() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try (Scanner sc = new Scanner(System.in)) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 加入相关的Handler
                            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));

                            pipeline.addLast(new GroupChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel channel = channelFuture.channel();

            // 给服务端发送消息
            this.sendMessage(sc, channel);
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    /**
     * 给服务端发送消息
     *
     * @param sc      Scanner
     * @param channel Channel
     */
    private void sendMessage(Scanner sc, Channel channel) {
        log.warn("client channel -> [{}]", channel.getClass().getSimpleName());
        // 客户端需要输入信息
        String message;
        do {
            message = sc.nextLine();
            channel.writeAndFlush(message);
        } while (!Objects.equals(message.trim(), CLOSE_STR));
    }

    public static void main(String[] args) throws Exception {
        GroupChatClient client = new GroupChatClient("127.0.0.1", 9090);
        client.run();
    }
}
