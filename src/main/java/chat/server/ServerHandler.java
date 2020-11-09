package chat.server;

import chat.GlobalChannel;
import chat.ProtocolHandle;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;

/**
 * 1000 channel建立,返回channelId
 * 1001 channel建立,发起的数据
 * 1002 发送/接收消息
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {

        // 获取客户端传输来的文本消息
        final JSONObject jsonObject = JSONUtil.parseObj(textWebSocketFrame.text());

        // 判断协议号
        switch (jsonObject.getStr("protocol")) {
            case "1000":
                ProtocolHandle.P1002(channelHandlerContext,jsonObject);
            case "1002":
                ProtocolHandle.P1002(channelHandlerContext,jsonObject);
            case "1004":
                ProtocolHandle.P1004(channelHandlerContext,jsonObject);
            case "1003":
                ProtocolHandle.P1004(channelHandlerContext,jsonObject);
            case "1005":
                ProtocolHandle.P1005(channelHandlerContext,jsonObject);
            default:
                System.out.println("nothing");
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 保存channel
        GlobalChannel.getOnline().remove(ctx.channel().id().toString(), ctx);
        // 告诉所有人
        GlobalChannel.getOnline().keySet().forEach(key -> {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("protocol", "1000");
            map.put("userId", key);
            // 返回当前用户列表
            map.put("userList", GlobalChannel.getOnline().keySet());
            GlobalChannel.getOnline().get(key).channel().writeAndFlush(new TextWebSocketFrame(JSONUtil.parse(map).toString()));
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 保存channel
        GlobalChannel.getOnline().put(ctx.channel().id().toString(), ctx);
    }
}
