package chat.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static Map<String, ChannelHandlerContext> online = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 获取客户端传输来的文本消息
        String text = textWebSocketFrame.text();
        // 收到消息,除了不给自己发,广播给其他人
        online.keySet().stream().
                filter(key->!key.equals(channelHandlerContext.channel().id().toString()))
                .forEach(key->online.get(key).channel().writeAndFlush(new TextWebSocketFrame(text)));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        online.put(ctx.channel().id().toString(), ctx);
    }
}
