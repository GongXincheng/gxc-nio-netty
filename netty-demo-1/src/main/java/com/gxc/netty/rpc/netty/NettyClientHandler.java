package com.gxc.netty.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author GongXincheng
 * @date 2020-02-29 23:51
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements Callable<Object> {

    /**
     * 上下文
     */
    private ChannelHandlerContext context;

    /**
     * 将来调用后返回的结果
     */
    private String result;

    /**
     * 调用方法的入参
     */
    private String param;

    /**
     * （2）
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * （1）与服务器连接创建后，被调用
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 需要在其他方法会使用到这个 ctx
        context = ctx;
    }

    /**
     * （4）收到服务器的数据后.
     *
     * @param ctx ChannelHandlerContext
     * @param msg msg
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, String msg) {
        // 获取返回结果
        result = msg;
        // 唤醒等待的线程
        notify();
    }


    /**
     * （3）->（5）被代理对象调用，发送数据给服务器，等待被唤醒。 -> 等待被唤醒(channelRead) -> 返回结果
     *
     * @return Object
     * @throws Exception Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        // 发送请求
        context.writeAndFlush(param);

        // 线程等待（channelRead 得到服务端返回值后唤醒该线程）
        wait();

        // 返回[服务端]返回结果
        return result;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

}
