package org.mindgazer.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author mindgazer
 * @date 2020/01/17
 */
public class NioClientHandler {

    private static final int BUF_SIZE = 1024;

    /**
     * 当一个新的Client连接上来后，Server掉用此方法来处理相应请求
     *
     * @param key 一个包含了Selector以及绑定到该Selector上的信道信息的对象
     * @throws IOException
     */
    public void handleAccept(SelectionKey key) throws IOException {
        // 一个Client连接到Server后，可以通过这个信道获取到对应的Client的信道
        // 这里别被搞晕了：Server和Client分别有各自的信道，下面的方法是通过Server端信道绑定的Selector，获取连接上来的Client端的信道
        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        clientChannel.configureBlocking(false);

        // 将Client端的信道，也注册到当前Server所使用的这个Selector上，同时绑定一个缓冲区，用于Server与Client读写数据
        // 在非阻塞模式下，读写操作都是异步的，所以必须有一个与Server和Client端信道绑定在一起的缓冲区被缓存起来，以便在双方交流的过程中随时调用
        // 而Selector可以将Server信道、Client端信道、他们读写所使用的缓冲区绑定在一起，避免混乱
        // 一旦连接创建，在与Selector绑定的时候，下一次需要绑定的事件就是读动作：OP_READ
        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(BUF_SIZE));
    }

    /**
     * 当Server中的某个信道接收到来自Client端的数据的时候，调用此方法
     *
     * @param key
     * @throws IOException
     */
    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        // 获取绑定在此信道上的缓冲区，读取Client端的数据
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = clientChannel.read(buf);
        // 如果读取数据长度返回-1，则代表另一端断开连接，那么本次会话也就结束了，关闭这个信道
        if (bytesRead == -1) {
            clientChannel.close();
        } else if (bytesRead > 0) {
            // 如果读取成功，则同时注册两个事件到Selector上：读、写
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    public void handleWrite(SelectionKey key) throws IOException {
        ByteBuffer buf = (ByteBuffer) key.attachment();
        // 修改缓冲区的状态：表示Buffer应当由读取数据转换为消费读取的数据的状态
        buf.flip();
        SocketChannel clientChannel = (SocketChannel) key.channel();
        clientChannel.write(buf);
        if (!buf.hasRemaining()) {
            // 消息都发出去了，那么下一次感兴趣的时间就是读取了
            key.interestOps(SelectionKey.OP_READ);
        }

        // 清理出更多空间供下一次读取
        buf.compact();
    }

}
