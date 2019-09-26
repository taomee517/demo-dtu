package com.fzk.demo.dtu.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 聊天室客户端初始化器
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:02
 */
public class DeviceInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(0,20,0, TimeUnit.MILLISECONDS));
        pipeline.addLast(new KT20Encoder());
        pipeline.addLast(new KT20Decoder());
//        pipeline.addLast(new DeviceHandler());
//        pipeline.addLast(new DeviceEncodeHandler());
        pipeline.addLast(new DeviceTestHandler());
    }
}
