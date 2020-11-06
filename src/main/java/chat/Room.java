package chat;

import chat.server.ChatRoomserver;

public class Room {

    public static void main(String[] args) throws Exception {


        // 开启服务端
        final ChatRoomserver chatRoomserver = new ChatRoomserver();
        chatRoomserver.bind(10087);


    }
}
