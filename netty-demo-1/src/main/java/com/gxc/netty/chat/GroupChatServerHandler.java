package com.gxc.netty.chat;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.format.DateTimeFormatter;

/**
 * GroupChat Server Handler
 *
 * @author GongXincheng
 * @date 2020-01-13 09:05
 */
@ChannelHandler.Sharable
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 定义一个Channel组，管理所有的Channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }

}
