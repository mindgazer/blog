package org.mindgazer.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 基于netty实现的客户端，需要完成的工作是：
 * （1）连接到服务器；（2）发送一个或者多个消息；（3）对于每个消息，等待并接收从服务器发回的相同的消息；（4）关闭连接。
 *
 * @author mindgazer
 * @date 2020/01/20
 */
public class SimpleNettyClient {

    private static String host = "127.0.0.1";
    private static int port = 8888;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 下面这行代码会以同步阻塞的方式等待与Server的连接直到建立成功
            ChannelFuture f = b.connect().sync();
            // 下面这行代码会一直阻塞，直到Channel关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }


}
