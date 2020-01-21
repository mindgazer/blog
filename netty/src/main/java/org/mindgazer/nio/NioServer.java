package org.mindgazer.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * @author mindgazer
 * @date 2020/01/17
 */
public class NioServer {

    private static final long SELECT_TIMEOUT = 10000;

    public static void main(String[] args) throws IOException {
        // 开启一个信道，监听8888端口
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(8888));
        // 通客户端一样，将信道设置为非阻塞模式
        serverChannel.configureBlocking(false);

        // 为一个信道注册一个Selector，注意：只有非阻塞模式的信道，才能绑定一个Selector
        Selector selector = Selector.open();
        // 在注册选择起得时候，明确指示该信道可以进行accept操作，这一步是必要的，否则选择器在轮询的时候将不会得到任何动作。所有的通信都是开始于创建连接
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 同BIO一样，我们创建一个类负责处理来自客户端的请求
        NioClientHandler handler = new NioClientHandler();
        while (true) {
            // 轮询注册到该Selector上的所有信道，如果有某个信道已经准备做某个I/O操作，则select方法会返回大于0的数字
            // select()方法是一个阻塞方法，如果传入一个超时时间，那么到了这个时间后会立即返回
            if (selector.select(SELECT_TIMEOUT) == 0) {
                System.out.print(".");
                continue;
            }

            // 如果有多个信道绑定在该Selector上，且有多个信道已经准备好做I/O操作，则selectedKeys会返回多个，这就是它为什么会返回迭代器的原因
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                SelectionKey key = keyIter.next();
                // 判断这个信道即将进行的I/O操作是否已经准备好与一个新的Client建立连接
                if (key.isAcceptable()) {
                    handler.handleAccept(key);
                }

                // 判断这个信道是已经准备好接收客户端的数据
                if (key.isReadable()) {
                    handler.handleRead(key);
                }

                // 判断这个信道是已经准备好写数据
                if (key.isValid() && key.isWritable()) {
                    handler.handleWrite(key);
                }

                keyIter.remove();
            }
        }
    }

}
