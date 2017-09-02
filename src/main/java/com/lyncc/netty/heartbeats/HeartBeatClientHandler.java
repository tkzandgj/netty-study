package com.lyncc.netty.heartbeats;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
            CharsetUtil.UTF_8));
    
    private static final int TRY_TIMES = 3;
    
    private int currentTime = 0;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("激活时间是："+new Date());
        System.out.println("HeartBeatClientHandler channelActive");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止时间是："+new Date());
        System.out.println("HeartBeatClientHandler channelInactive");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("循环触发时间："+new Date());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                if(currentTime <= TRY_TIMES){   //超过这个次数没有向服务端去发送消息的时候   那么服务端就会去监控
                    //下面客户端的数据可以看出来    在第五次的时候已经大于TRY_TIMES    所以不向服务端发送消息了
                    /*激活时间是：Sat Sep 02 10:47:25 GMT+08:00 2017
                    HeartBeatClientHandler channelActive
                    循环触发时间：Sat Sep 02 10:47:29 GMT+08:00 2017
                    currentTime:0
                    循环触发时间：Sat Sep 02 10:47:33 GMT+08:00 2017
                    currentTime:1
                    循环触发时间：Sat Sep 02 10:47:37 GMT+08:00 2017
                    currentTime:2
                    循环触发时间：Sat Sep 02 10:47:41 GMT+08:00 2017
                    currentTime:3
                    循环触发时间：Sat Sep 02 10:47:45 GMT+08:00 2017
                    循环触发时间：Sat Sep 02 10:47:49 GMT+08:00 2017
                    循环触发时间：Sat Sep 02 10:47:53 GMT+08:00 2017*/
                    System.out.println("currentTime:"+currentTime);
                    currentTime++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        System.out.println(message);
        if (message.equals("Heartbeat")) {
            ctx.write("has read message from server");
            ctx.flush();
        }
        ReferenceCountUtil.release(msg);
    }
}
