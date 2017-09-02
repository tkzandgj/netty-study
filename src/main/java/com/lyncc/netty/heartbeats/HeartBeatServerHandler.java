package com.lyncc.netty.heartbeats;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    private int loss_connect_time = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                System.out.println("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
            //客户端没有发送消息之后，   服务端就会打印没有接收到客户端的消息
            /*/127.0.0.1:51877->Server :Heartbeat
            server channelRead..
            /127.0.0.1:51877->Server :Heartbeat
            server channelRead..
            /127.0.0.1:51877->Server :Heartbeat
            server channelRead..
            /127.0.0.1:51877->Server :Heartbeat
            5 秒没有接收到客户端的信息了
            5 秒没有接收到客户端的信息了
            5 秒没有接收到客户端的信息了*/
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channelRead..");
        System.out.println(ctx.channel().remoteAddress() + "->Server :" + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
