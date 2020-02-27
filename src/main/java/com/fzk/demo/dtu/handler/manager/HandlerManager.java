package com.fzk.demo.dtu.handler.manager;

import com.fzk.demo.dtu.entity.Device;
import com.fzk.demo.dtu.handler.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 处理器管理
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:02
 */
public class HandlerManager extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new DeviceLogRecorder());
        pipeline.addLast(new FrameSplitHandler());
        pipeline.addLast(new UnEscapeHandler());
        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new IdleStateHandler(0,0,150000, TimeUnit.MILLISECONDS));
        pipeline.addLast(new CoreLogicHandler(new Device()));
    }
}
