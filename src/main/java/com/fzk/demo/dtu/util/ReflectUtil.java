package com.fzk.demo.dtu.util;

import com.fzk.demo.dtu.constant.AttachFunType;
import com.fzk.demo.dtu.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.fzk.demo.dtu.constant.DefaultValue.FUN_TAG_CONNECTOR;
import static com.fzk.demo.dtu.constant.DefaultValue.PARA_CONNECTOR;

@Slf4j
public class ReflectUtil {
    private static Map<String,Boolean> SUPPORTED_MAP = new HashMap<>();

    public static boolean checkTagExist(Device device, String tag){
        try {
            Class<?> clazz = device.getClass();
            String methodName = StringUtils.join("get", tag.toUpperCase());
            Method method = clazz.getMethod(methodName);
            if(Objects.nonNull(method)){
                return true;
            }
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static String buildAttachMessage(AttachFunType type, Device device, String tag) throws Exception{
        boolean tagExist = checkTagExist(device,tag);
        if(!tagExist){
            return buildFzkContent(device.attachId,AttachFunType.PUBLISH.getFunId(),"b443",tag);
        }
        String msg = null;
        if (AttachFunType.PUBLISH_ACK.equals(type)) {
            msg = buildFzkContent(device.attachId,AttachFunType.PUBLISH_ACK.getFunId(),tag,null);
        } else {
            msg = buildFzkContent(device.attachId,type.getFunId(),tag,getTagInfo(device,tag));
        }
        return msg;
    }

    public static String getTagInfo(Device device, String tag) throws Exception{
        Class<?> clazz = device.getClass();
        String methodName = StringUtils.join("get", tag.toUpperCase());
        Method method = clazz.getMethod(methodName);
        if (Objects.nonNull(method)) {
            String tagInfo = ((String) method.invoke(device));
            return tagInfo;
        }
        return null;
    }

    public static void tagSetting(Device device, String tag, String value) throws Exception{
        if (checkTagExist(device,tag)) {
            Class<?> clazz = device.getClass();
            String methodName = StringUtils.join("set", tag.toUpperCase());
            Method method = clazz.getMethod(methodName,String.class);
            if (Objects.nonNull(method)) {
                method.invoke(device,value);
            }
        }
    }

    public static String buildFzkContent(int attachId, int fun, String tag, String value){
        if (StringUtils.isNotEmpty(value)) {
            return StringUtils.join(attachId, PARA_CONNECTOR,fun,FUN_TAG_CONNECTOR,tag,PARA_CONNECTOR,value);
        }else {
            return StringUtils.join(attachId, PARA_CONNECTOR,fun,FUN_TAG_CONNECTOR,tag);
        }
    }
}
