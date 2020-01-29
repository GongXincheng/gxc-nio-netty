package com.gxc.netty.chat;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GroupChat Server Handler
 *
 * @author GongXincheng
 * @date 2020-01-13 09:05
 */
@Slf4j
@ChannelHandler.Sharable
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 定义一个Channel组，管理所有的Channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * handlerAdded -> 表示连接建立，一旦连接第一个被执行
     * 将当前 Channel 加入到 ChannelGroup
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // TODO 将该客户端加入聊天的信息，推送给其他在线的客户端
        //  该方法会将 channelGroup 中所有的 channel 遍历，并发送消息(不需要自己遍历)
        channelGroup.writeAndFlush("[客户端：[" + channel.remoteAddress() + "] 加入聊天]");
        channelGroup.add(channel);
    }

    /**
     * 表示 Channel 处于活动状态（上线）
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.warn("|--> {}，上线", ctx.channel().remoteAddress());
    }

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * handlerRemoved -> 表示断开连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端：[" + channel.remoteAddress() + "] 离开聊天室");

        // 会自动移除掉...
        // channelGroup.remove(channel);
        log.warn("channelGroup 当前Size：[{}]", channelGroup.size());
    }

    /**
     * 表示 Channel 处非活动状态（离线）
     * 断开连接，将客户端离开信息推送给当前在线的客户
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("|--> {}，离线", ctx.channel().remoteAddress());
    }

    ////////////////////////////////////////////////////////////////////////////////////


    /**
     * 读取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        // 遍历 ChannelGroup，根据不同的情况，回送不同的消息
        channelGroup.forEach(ch -> {
            // 如果不是当前的Channel，则直接转发
            if (channel != ch) {
                // 拼接内容
                String message = FORMAT.format(LocalDateTime.now()) + " [" + channel.remoteAddress() + "]：\n" + msg + "\n";
                ch.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
            } else {
                // 自己发送消息回显
                ch.writeAndFlush(Unpooled.copiedBuffer("[自己]：" + msg + "\n", CharsetUtil.UTF_8));
            }
        });
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭通道
        ctx.close();
    }
}
