package com.gxc.netty.simple.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 说明：
 * 自定义一个 Handler 需要继承 Netty 规定好的某个 HandlerAdapter（规范）
 * 这时我们自定义一个 Handler 才能称为一个handler
 *
 * @author GongXincheng
 * @date 2019-12-22 20:46
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private List<SocketChannel> clientChannelList;

    public NettyServerHandler(List<SocketChannel> clientChannelList) {
        this.clientChannelList = clientChannelList;
    }

    /**
     * 读取数据事件（这里可以读取客户端发送的消息）
     * 1：ChannelHandlerContext ctx：上下文对象,含有管道 PipeLine、通道 Channel、连接地址
     * 2：Object msg：就是客户端发送的数据 默认是Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("|-- Server ctx = " + ctx);

        // 任务队列
        // TODO：1：用户程序自定义的普通任务（将任务放入处理当前Channel事件的WorkerGroup线程组中之一的NioEventLoop的taskQueue中）
        //  通过ChannelHandlerContext获取PipeLine然后获取Channel，再通过Channel获取NioEventLoop为taskQueue队列添加任务
        ctx.pipeline().channel().eventLoop().execute(() -> {
            try {
                long sleepTime = 1000L;
                Thread.sleep(sleepTime);
                ctx.writeAndFlush(Unpooled.copiedBuffer("我睡醒了！[" + sleepTime + "]ms", CharsetUtil.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //  TODO：2：用户自定义定时任务（该任务提交到 scheduleTaskQueue）
        ctx.channel().eventLoop().schedule(() -> {
            ctx.writeAndFlush(Unpooled.copiedBuffer("我是定时任务！", CharsetUtil.UTF_8));
        }, 10, TimeUnit.SECONDS);


        //  TODO：3：非当前Reactor线程调用Channel的各种方法
        for (SocketChannel socketChannel : clientChannelList) {
            // 排除自身
            if (socketChannel == ctx.channel()) {
                break;
            }
            socketChannel.eventLoop().execute(() -> {
                String message = String.format("我是%s：%s", ctx.channel().remoteAddress(), "发送给其他Client通道");
                socketChannel.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
            });
        }

        // 将 msg 转成 ByteBuf( Netty 提供的, 不是 NIO 提供的 )
        ByteBuf buffer = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + buffer.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕.
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush：是  write & flush
        // 将数据写入到缓冲, 并刷新
        // 一般讲，我们对这个发送的数据进行编码
        String message = "Hello Netty";
        ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }

    /**
     * 处理异常（一般需要关闭通道）
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
