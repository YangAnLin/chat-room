package chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatRoomserver {

    public void bind(int port) throws Exception {

        //bossGroup就是parentGroup，是负责处理TCP/IP连接的
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //workerGroup就是childGroup,是负责处理Channel(通道)的I/O事件

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) //初始化服务端可连接队列,指定了队列的大小128
                .childOption(ChannelOption.SO_KEEPALIVE, true) //保持长连接
                .childHandler(new ChannelInitializer<SocketChannel>() {  // 绑定客户端连接时候触发操作
                    @Override
                    protected void initChannel(SocketChannel sh) throws Exception {
                        sh.pipeline()
                                // websocket是基于http协议的，所以需要使用http编解码器
                                .addLast(new HttpServerCodec())
                                // 对写大数据流的支持
                                .addLast(new ChunkedWriteHandler())
                                // 对http消息的聚合，聚合成FullHttpRequest或FullHttpResponse
                                // 在Netty的编程中，几乎都会使用到这个handler
                                .addLast(new HttpObjectAggregator(1024 * 64))
                                // 以上三个处理器是对http协议的支持

                                // websocket 服务器处理的协议，并用于指定客户端连接的路由(这里指定的是 /ws)
                                // 这里的URL就是 ws://ip:port/ws
                                // 该处理器为运行websocket服务器承担了所有繁重的工作
                                // 它会负责websocket的握手以及处理控制帧
                                // websocket的数据传输都是以frames进行的
                                .addLast(new WebSocketServerProtocolHandler("/ws"))
                                .addLast(new ServerHandler()); //使用ServerHandler类来处理接收到的消息
                    }
                });
        //绑定监听端口，调用sync同步阻塞方法等待绑定操作完
        ChannelFuture future = sb.bind(port).sync();

        if (future.isSuccess()) {
            System.out.println("服务端启动成功");
        } else {
            System.out.println("服务端启动失败");
            future.cause().printStackTrace();
            bossGroup.shutdownGracefully(); //关闭线程组
            workerGroup.shutdownGracefully();
        }

        //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
        future.channel().closeFuture().sync();

    }


}
