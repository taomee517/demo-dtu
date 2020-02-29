package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.constant.AttachFunType;
import com.fzk.demo.dtu.entity.Device;
import com.fzk.demo.dtu.entity.MessageBasic;
import com.fzk.demo.dtu.util.MessageBuilder;
import com.fzk.demo.dtu.util.ReflectUtil;
import com.fzk.dtu.constant.DownMsgType;
import com.fzk.dtu.constant.UpMsgType;
import com.fzk.dtu.utils.BytesUtil;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.fzk.demo.dtu.constant.DefaultValue.*;

/**
 * 模拟聊天室客户端消息处理类
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:47
 */
@Slf4j
public class TestMessageHandler extends ChannelInboundHandlerAdapter {
    private Device device;

    public TestMessageHandler(Device device) {
        this.device = device;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                alarmMessageTest(ctx);
            }
        },10, TimeUnit.SECONDS);
    }

    private void alarmMessageTest(ChannelHandlerContext ctx){
        try {
            //测试告警消息
            String resultContent = "3,1#";
            ReflectUtil.tagSetting(device,ALARM_TAG,resultContent);
            String alarmMsg = ReflectUtil.buildAttachMessage(AttachFunType.PUBLISH,device,ALARM_TAG);
            String packResult = MessageBuilder.buildFzkMsg(device.sn,alarmMsg,true);
            log.info("透传↑↑↑↑：{}", alarmMsg);
            ctx.writeAndFlush(packResult);
            ctx.pipeline().remove("test");
        } catch (Exception e) {
            log.error("构造告警消息时发生异常：{}", e);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("EventTriggerHandler发生异常：", cause);
        ctx.close();
    }

}
