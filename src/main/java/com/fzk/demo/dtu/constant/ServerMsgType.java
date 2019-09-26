package com.fzk.demo.dtu.constant;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum  ServerMsgType {
    HEART_BEAT("7E80010005", "保活"),
    LOGIN_RESP("7E8100", "登录回复"),

    CONTROL_LOCK("3723623530312C31", "控车-上锁"),
    CONTROL_UNLOCK("3723623530312C32", "控车-解锁"),
    CONTROL_CAR_SEARCH("3723623530312C33", "控车-寻车"),
    CONTROL_FIRE("3723623530312C35", "控车-点火"),
    CONTROL_MISFIRE("3723623530312C36", "控车-熄火"),
    CONTROL_WIN_CLOSE("3723623530312C37", "控车-关窗"),
    CONTROL_WIN_OPEN("3723623530312C38", "控车-开窗"),
    CONTROL_OIL_ACCESS("3723623530312C3B", "控车-通油"),
    CONTROL_OIL_BREAK("3723623530312C3C", "控车-断油"),
    CONTROL_FORCE_OIL_BREAK("3723623530312C3D", "控车-强制断油"),


    QUERY_VERSION("312362313031", "查询版本"),
    QUERY_ABILITY("312362313032", "查询能力"),
    QUERY_STATUS("312362333031", "查询总状态"),
    ;

    /**消息的独有特征*/
    private String feature;

    /**该消息的功能*/
    private String target;

    /**默认回复消息*/
//    private String defaultResp;


    private static Map<String, ServerMsgType> featureMap = new HashMap<>();


    ServerMsgType(String feature, String target){
        this.feature = feature;
        this.target = target;
//        this.defaultResp = resp;
    }

    public static ServerMsgType getTypeByFeature(String feature){
       return featureMap.get(feature);
    }

    public static String generateControlResult(ServerMsgType msgType,Boolean succ){
        String funcAndTag = "372362353031";
        String respFuncAndTag = "3723623530312C3";
        if(msgType.getFeature().contains(funcAndTag)){
            String defaultResp = "7E0900000E014533224352037B41362C3723623430312C322C3223007E";
            char respFeature = msgType.getFeature().charAt(msgType.getFeature().length()-1);
            String succStr = succ ? "31" : "30";
            String result = respFeature + succStr;
            String resp = defaultResp.replace("3723623430312C322C32" , respFuncAndTag + result);
            return resp;
        }
        return null;
    }

    static {
        ServerMsgType[] msgs = values();
        for(ServerMsgType msg : msgs){
            featureMap.put(msg.getFeature(),msg);
        }
    }

}
