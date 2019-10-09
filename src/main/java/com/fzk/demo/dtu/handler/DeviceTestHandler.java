package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.util.MessageBuilder;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceTestHandler extends ChannelInboundHandlerAdapter {
    //7E090008600145332243520006FF4607F2513252077F6AF7D97054A7E1D0D0C53F08BF6950481DBF04CEF9917F1A594203AD6300ED5546AA122FBB340B914681E0398EDA1957DD91F4A09BCD2B0BE7EC436B32677D02292F05F326319060EEC478E8008B47F172BA8EFE0D999A63D47E

    private int index = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String serverMsg = ((String) msg);
        log.info("服务器回复：{}", serverMsg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        if(event.equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
            if(index == 0){
                Thread.sleep(3000);
            }

            if (index < 200) {
                //b301
//                String msgContent = "41362C3723623330312C303030302C31313131312C313030302C30303030302C30302C32302C302C2C2C2C3830303030302C2C3463352C3133322C2C2C302C383030302C4F2C2C383030302C383030302C2C653823";
                //b451
                String msgContent = "41362C3523623435312C302C36342C31313339352E3530302C3030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303023";
                String snNo = "14533224352";
                String srcMsgId = "0900";
                byte[] content = BytesTranUtil.hexStringToBytes(msgContent);
                boolean aesEncode = true;
                String upMsg = MessageBuilder.buildMsg(snNo,srcMsgId,content,index,aesEncode);
                log.info("压测消息: {}", upMsg);
                ctx.channel().writeAndFlush(upMsg);
            }
            index ++;
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //注册消息
//        String in = "7E010000210145332243520001002C012F37303131314B542D32302020206342440257666501D4C14238383838381C7E";

        //登录消息
        String in = "7E 01 02 08 20 01 45 33 22 43 52 00 21 46 09 7D F6 86 5E 66 E4 11 B6 AD 8A EE 92 D3 11 42 EC 8B 51 D6 31 9A E7 2A 6F C8 56 C4 3F C1 6F 8E 7E";

        log.info("模拟设备注册消息: {}", in);
        ctx.channel().writeAndFlush(in);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("DeviceHandler发生异常：", cause);
        ctx.close();
    }
}
