package chat.server;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

/**
 * 1000 channel建立,返回channelId
 * 1001 channel建立,发起的数据
 * 1002 发送/接收消息
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Map<String, ChannelHandlerContext> online = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 获取客户端传输来的文本消息
        final JSONObject jsonObject = JSONUtil.parseObj(textWebSocketFrame.text());

        final String userId = channelHandlerContext.channel().id().toString();

        // 判断协议号
        if(jsonObject.getStr("protocol").equals("1002")){
            // 收到消息,广播给其他人,包括自己
            online.keySet().forEach(key -> {
                final HashMap<String, String> map = new HashMap<>();
                map.put("protocol", "1002");
                map.put("msg", jsonObject.getStr("msg"));
                map.put("userId", userId);
                online.get(key).channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(map)));
            });
        }

        if(jsonObject.getStr("protocol").equals("1000")){
            final HashMap<String, Object> map = new HashMap<>();
            map.put("protocol", "1000");
            map.put("userId", userId);
            // 返回当前用户列表
            map.put("userList", online.keySet());
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.parse(map).toString()));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 保存channel
        online.remove(ctx.channel().id().toString(), ctx);
        // 告诉所有人
        online.keySet().forEach(key -> {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("protocol", "1000");
            map.put("userId", key);
            // 返回当前用户列表
            map.put("userList", online.keySet());
            online.get(key).channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.parse(map).toString()));
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 保存channel
        online.put(ctx.channel().id().toString(), ctx);
    }
}
