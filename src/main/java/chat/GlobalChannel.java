package chat;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储全局channel
 */
@Data
public class GlobalChannel {

    /**
     * 在线用户
     */
    static Map<String, ChannelHandlerContext> online = new HashMap<>();

    public static Map<String, ChannelHandlerContext> getOnline() {
        return online;
    }

    /**
     * 正在输入仲...
     */
    static final List<String> writings = new ArrayList<>();

    public static List<String> getWritings() {
        return writings;
    }
}
