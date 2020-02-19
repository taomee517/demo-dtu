package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.constant.DefaultValue;
import com.fzk.demo.dtu.entity.Device;
import com.fzk.demo.dtu.entity.MessageBasic;
import com.fzk.demo.dtu.util.MessageBuilder;
import com.fzk.dtu.constant.DownMsgType;
import com.fzk.dtu.constant.UpMsgType;
import com.fzk.dtu.utils.BytesUtil;
import com.fzk.sdk.util.BytesTranUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.fzk.demo.dtu.constant.DefaultValue.*;

/**
 * 模拟聊天室客户端消息处理类
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:47
 */
@Slf4j
public class CoreLogicHandler extends ChannelInboundHandlerAdapter {
    private Device device;

    private int index;
    private int shardIndex;
    private int shardValidIndex;
    private boolean isOnline = false;

    public CoreLogicHandler(Device device) {
        this.device = device;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof MessageBasic){
            MessageBasic in = ((MessageBasic) msg);
            String hexMsg = BytesUtil.bytesToHexShortString(in.raw);
            log.info("平台消息：{}", hexMsg);
            int funId = in.funId;
            if(funId== DownMsgType.TRANSMIT.getMsgId()){
                String fzkContent = new String(in.content);
                log.info("透传↓↓↓↓：{}", fzkContent);
            }else {
                ByteBuffer buffer = ByteBuffer.wrap(in.content);
                DownMsgType type = DownMsgType.getProtocolByMsgId(funId);
                switch (type){
                    case RESPONSE:
                        byte srcResult = buffer.get(4);
                        if(!isOnline && srcResult==0){
                            isOnline = true;
                            log.info("登录成功！");
                            String versionContent = buildFzkContent(PUBLISH_FUN,VERSION_TAG,device.attachVersion);
                            String versionMsg = MessageBuilder.buildFzkMsg(device.sn,versionContent,true);
                            log.info("透传↑↑↑↑：{}", versionContent);
                            ctx.writeAndFlush(versionMsg);

                            String abilityContent = buildFzkContent(PUBLISH_FUN,ABILITY_TAG,device.ability);
                            String abilityMsg = MessageBuilder.buildFzkMsg(device.sn,abilityContent,true);
                            log.info("透传↑↑↑↑：{}", abilityContent);
                            ctx.writeAndFlush(abilityMsg);
                        }
                        break;
                    case REGISTER_ACK:
                        byte[] srcAuth = new byte[in.content.length-3];
                        buffer.position(3);
                        buffer.get(srcAuth);
                        String authKey = new String(srcAuth,CHARSET);
                        device.authKey = authKey;
                        if (!isOnline) {
                            String loginMsg = buildLoginMessage();
                            log.info("模拟登录");
                            ctx.writeAndFlush(loginMsg);
                        }
                        break;
                    case QUERY:
                    default:
                        log.info("暂时不支持的消息类型， funId = {}", funId);
                        break;
                }
            }
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        if(event.equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
            String heatbeat = MessageBuilder.buildMsg(device.sn,UpMsgType.HEART_BEAT.getMsgId(),null,true);
            log.info("C ——> S");
            ctx.writeAndFlush(heatbeat);
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String first = null;
        if (Objects.isNull(device.authKey)) {
            byte[] registerContent = BytesTranUtil.hexStringToBytes(device.regContent);
            String registerMsg = MessageBuilder.buildMsg(device.sn, UpMsgType.REGISTER.getMsgId(),registerContent,true);
            first = registerMsg;
            log.info("模拟注册");
        }else {
            first = buildLoginMessage();
            log.info("模拟登录");
        }

        ctx.writeAndFlush(first);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("CoreLogicHandler发生异常：", cause);
        ctx.close();
    }



    private String buildLoginMessage(){
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(device.authKey.getBytes(CHARSET));
        buffer.writeBytes(BytesTranUtil.str2Bcd(device.imei));
        byte[] authContent = new byte[buffer.readableBytes()];
        buffer.readBytes(authContent);
        String authMsg = MessageBuilder.buildMsg(device.sn,UpMsgType.AUTH.getMsgId(),authContent,true);
        buffer.release();
        return authMsg;
    }


    private String buildFzkContent(int fun, String tag,String value){
        if (StringUtils.isNotEmpty(value)) {
            return StringUtils.join(device.attachId, PARA_CONNECTOR,fun,FUN_TAG_CONNECTOR,tag,PARA_CONNECTOR,value);
        }else {
            return StringUtils.join(device.attachId, PARA_CONNECTOR,fun,FUN_TAG_CONNECTOR,tag);
        }
    }
}
