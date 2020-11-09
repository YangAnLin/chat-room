package chat;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;

/**
 * 协议处理器
 */
public class ProtocolHandle {


    /**
     * 群聊
     */
    public static void P1002(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {
        final HashMap<String, Object> map = new HashMap<>();
        // 协议号
        map.put("protocol", "1002");
        // 消息号
        map.put("msgId", jsonObject.getStr("msgId"));
        // 消息
        map.put("msg", jsonObject.getStr("msg"));
        // 用户Id
        map.put("userId", channelHandlerContext.channel().id().toString());
        MsgHandle.pushAll(map);
    }

    /**
     * 添加正在输入中....
     */
    public static void P1004(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {
        // 加入集合
        GlobalChannel.getWritings().add(channelHandlerContext.channel().id().toString());
        // 广播所有人
        final HashMap<String, Object> map = new HashMap<>();
        // 协议号
        map.put("protocol", "1004");
        // 用户Id
        map.put("writings", GlobalChannel.getWritings());
        MsgHandle.pushAll(map);
    }

    /**
     * 删除正在输入中....
     */
    public static void P1005(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {

        // 加入集合
        GlobalChannel.getWritings().remove(channelHandlerContext.channel().id().toString());

        // 广播所有人
        final HashMap<String, Object> map = new HashMap<>();
        // 协议号
        map.put("protocol", "1004");
        // 用户Id
        map.put("writings", GlobalChannel.getWritings());
        MsgHandle.pushAll(map);
    }

    /**
     * 私聊
     */
    public static void P1003(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {

        final String userId = channelHandlerContext.channel().id().toString();
        final HashMap<String, String> map = new HashMap<>();
        map.put("protocol", "1002");
        map.put("msg", jsonObject.getStr("msg"));
        map.put("userId", userId);
        MsgHandle.pushAll(map);
    }

    /**
     * 私聊
     */
    public static void P1000(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject) {

        final String userId = channelHandlerContext.channel().id().toString();

        // 第一次进入房间
        final HashMap<String, Object> map = new HashMap<>();
        map.put("protocol", "1000");
        map.put("userId", userId);
        // 返回当前用户列表
        map.put("userList", GlobalChannel.getOnline().keySet());
        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.parse(map).toString()));
    }


}
