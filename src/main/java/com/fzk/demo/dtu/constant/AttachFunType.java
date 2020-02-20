package com.fzk.demo.dtu.constant;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum AttachFunType {
    QUERY(1, "查询", 2),
    QUERY_ACK(2, "查询回复", null),

    SETTING(3, "设置",4),
    SETTING_ACK(4, "设置回复", null),
    WRITE(5, "执行", 5),
    PUBLISH(7, "发布",8),
    PUBLISH_ACK(8, "发布回复",null),
    ;

    /**功能号*/
    private int funId;

    /**功能说明*/
    private String desc;


    private Integer ackFunId;


    private static Map<Integer, AttachFunType> ID_MAP = new HashMap<>();


    AttachFunType(int funId, String desc, Integer ackFunId){
        this.funId = funId;
        this.desc = desc;
        this.ackFunId = ackFunId;
    }

    public static AttachFunType getTypeByFunId(Integer funId){
       return ID_MAP.get(funId);
    }

    static {
        AttachFunType[] msgs = values();
        for(AttachFunType msg : msgs){
            ID_MAP.put(msg.funId,msg);
        }
    }

}
