package com.fzk.demo.dtu;

import com.fzk.demo.dtu.constant.DefaultValue;
import com.fzk.demo.dtu.handler.manager.HandlerManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 模拟DTU客户端
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:44
 */
public class DTUClient {
    public static void main(String[] args) throws Exception{
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(DefaultValue.DEFAULT_WORKER_THREAD);
        try {
            String host = "127.0.0.1";
//            String host = "pre.acceptor.mysirui.com";
//            String host = "acceptor.mysirui.com";

//            String host = "192.168.6.183";
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new HandlerManager());
            ChannelFuture future = bootstrap.connect(host, 2120).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
