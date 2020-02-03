package com.gxc.netty.websocket;

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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * WebSocket测试
 *
 * @author GongXincheng
 * @date 2020-02-02 18:03
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

                            // 因为是基于Http协议，使用 Http 的编码和解码器
                            pipeline.addLast(new HttpServerCodec());

                            // 是以块方式写，添加 ChunkedWrite 处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /*
                               说明：
                                 1：Http 数据在传输过程中是分段，HttpObjectAggregator 就是将多个段聚合起来
                                 2：这就是为什么当浏览器发送大量数据时，就会发生多次http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /*
                               说明：
                                 1：对于 WebSocket，他的数据是以 帧(frame) 形式传递
                                 2：可以看到 WebSocketFrame 下面有6个子类
                                 3：浏览器发送请求时 ws://localhost:9090/hello 表示请求URI
                                 4：WebSocketServerProtocolHandler 核心功能，将 Http 协议升级为 WS 协议， 保持长连接
                                 5：通过一个状态码 101
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 自定义 Handler 处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
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
