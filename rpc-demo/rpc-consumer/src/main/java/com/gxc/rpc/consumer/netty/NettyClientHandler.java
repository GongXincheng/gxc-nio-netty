package com.gxc.rpc.consumer.netty;

import com.gxc.rpc.core.protocol.RpcRequest;
import com.gxc.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author GongXincheng
 * @date 2020-03-01 21:41
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements Callable<RpcResponse> {

    private static final NettyClientHandler INSTANCE = new NettyClientHandler();

    private NettyClientHandler() {}

    public static NettyClientHandler getInstance() {
        return INSTANCE;
    }

    private ChannelHandlerContext context;

    private RpcResponse response;

    private RpcRequest request;

    public void setRequest(RpcRequest request) {
        this.request = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        // 得到返回结果
        log.info("Client get server response!");
        response = msg;
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public synchronized RpcResponse call() throws Exception {
        context.writeAndFlush(request);
        wait();
        return response;
    }
}
