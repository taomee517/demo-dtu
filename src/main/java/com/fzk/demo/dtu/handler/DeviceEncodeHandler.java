package com.fzk.demo.dtu.handler;

import com.fzk.demo.dtu.constant.ServerMsgType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 模拟聊天室客户端消息处理类
 *
 * @Author luotao
 * @E-mail taomee517@qq.com
 * @Date 2019\1\27 0027 16:47
 */
@Slf4j
public class DeviceEncodeHandler extends ChannelInboundHandlerAdapter {
    private static final String SIGN_RESP_PREFIX = "7E8100";
    private static final String HEART_BEAT_PREFIX = "7E80010005";
    private static final String TRANSMIT_MSG_PREFIX = "7E8900";
    private static final String CONTROL_CMD_SIGN = "372362353031";
    private static final String QUERY_VERSION_SIGN = "312362313031";
    private int index;
    private int shardIndex;
    private int shardValidIndex;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String target = null;
        String resp = null;
        String serverMsg = msg.toString();

        if(serverMsg.contains(SIGN_RESP_PREFIX)){
            log.info("服务器登录回复：{}",serverMsg);
            serverMsg = null;
        }else if(serverMsg.contains(HEART_BEAT_PREFIX)){
            log.info("心跳 ↓");
            serverMsg = null;
        }

        if (index==0) {
            //第一条消息，发送一个定位
            Thread.sleep(4000);
            String up = "7E02000840014533224352000497089B87B32C2629B87D02AA57D700BFC1DBB1C5A8A7C7CB4B5B59859FDB6622B8AB9B351C3C7F316235FC8AD2458FDC52F1E047447FA8879F9B0DE948041820D48D7E";
            log.info("设备上报定位消息： {}", up);
            ctx.channel().writeAndFlush(up);
            index ++;
        }

        if (StringUtils.isNotEmpty(serverMsg)) {
            if (serverMsg.contains(TRANSMIT_MSG_PREFIX)){
                int featureStartIndex = serverMsg.indexOf("362C") + 5;
                int featureEndIndex = serverMsg.contains("62353031")?(featureStartIndex + 12):(featureStartIndex + 8);
                String feature = serverMsg.substring(featureStartIndex, featureEndIndex);
                ServerMsgType serverMsgType = ServerMsgType.getTypeByFeature(feature);
                if (!Objects.isNull(serverMsg)) {
                    switch (serverMsg){
                        case HEART_BEAT_PREFIX:
                            log.info("心跳-SERVER ↓");
                            break;
                        default:
                            target = serverMsgType.getTarget();
                            log.info("服务器 {} ：{}",target, serverMsg);
                            break;
                    }
                    serverMsg = null;
                }
//                if(serverMsg.contains(CONTROL_CMD_SIGN)){
//                    target = "控制-解锁";
//                    log.info("透传消息,{}：{}",target,serverMsg);
//                    serverMsg = null;
//                    resp = "7E0900000E014533224352037B41362C3723623430312C322C3223007E";
//                    log.info("设备回复 {} 结果：{}",target, resp);
//                    ctx.channel().writeAndFlush(resp);
//                }else if (serverMsg.contains(TRANSMIT_MSG_PREFIX) && serverMsg.contains(QUERY_VERSION_SIGN)){
//                    target = "查询b101";
//                    log.info("透传消息,{}：{}",target,serverMsg);
//                    serverMsg = null;
//                    resp = "7E0900086001453322435200081D08941CA96C42BD24321F704B27C82A75F040925EE2B3D35F5B6EA441E091405FCD7BD0D0572624AE7A21E274AF9E35D1B9D66BC7A692F52183AEE8ADAB2B6ECEA81C684E9945900FA7AD2271E0657A7D8A220E4FA8927A4664B6AB17A1350C3A7E";
//                    log.info("设备回复 {} 结果：{}",target, resp);
//                    ctx.channel().writeAndFlush(resp);
//                }
            }
        }

        //其他类型的服务器消息
        if (StringUtils.isNotEmpty(serverMsg)) {
            log.info("服务器消息: {}", serverMsg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent)evt;
        if(event.equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
            String heatbeat = "7E0002080001453322435200034D7E";
            log.info("心跳 ↑");
            ctx.channel().writeAndFlush(heatbeat);
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String in = "7E0100083001453322435200014BEEC3AFAFA91355C559EF56683640AFBF5E700349C0DFD0C08B3FBC2D440DA8F202DD2F418EFBEDE7CD0A4372B3A75FF07E";
        log.info("模拟设备注册消息: {}", in);
        ctx.channel().writeAndFlush(in);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("DeviceHandler发生异常：", cause);
        ctx.close();
    }
}
