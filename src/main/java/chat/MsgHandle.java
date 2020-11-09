package chat;

import cn.hutool.json.JSONUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送消息,处理器
 */
public class MsgHandle {

    /**
     * 推送给所有人,包括自己
     */
    public static void pushAll(Object map){
        GlobalChannel.getOnline().keySet().forEach(key -> {
            GlobalChannel.getOnline().get(key).channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(map)));
        });

    }

    /**
     * 推送给所有人,不包括自己
     */
    public static void pushAllOnMe(){

    }

    /**
     * 推送给指定聊天室
     */
    public  static void pushSpecialRoom(){

    }

    /**
     * 推送给指定用户
     */
    public static void pushSpecialUser(){

    }

}
