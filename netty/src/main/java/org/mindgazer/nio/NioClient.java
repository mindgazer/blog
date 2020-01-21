package org.mindgazer.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author mindgazer
 * @date 2020/01/17
 */
public class NioClient {

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        // 我们要将该信道设置为非阻塞模式，这里传入false。非阻塞模式下，调用任何一个方法总是会立即返回
        // 可以看出，NIO也支持阻塞模式
        clientChannel.configureBlocking(false);

        System.out.print("connecting to the server");
        // 在非阻塞模式下，connect方法会立即返回，并且可能会在连接创建之前就返回，故此我们必须主动去判断连接是否已经创建
        if (!clientChannel.connect(new InetSocketAddress("127.0.0.1", 8888))) {
            // 轮询判断连接是否成功建立
            // 虽然这里也是使用while来实现的忙等，但这段代码是为了演示在非阻塞下，connect之后我们可以作别的事情
            while (!clientChannel.finishConnect()) {
                System.out.print(".");
                // 我们可以在这里做别的事情
            }
        }

        // 先输出一个回车
        System.out.println();

        // 创建读缓冲区，接收来自client的数据
        // ByteBuffer必须通过静态方法来显示开辟一个空间
        ByteBuffer readBuf = ByteBuffer.allocate(1024);

        // 创建写缓冲区，并将消息写入到该缓冲区中
        // ByteBuffer也可以通过已有的Byte数组来初始化，这样其长度与初始化的字节数组长度相同
        byte[] message = "hello nio!".getBytes("UTF-8");
        ByteBuffer writeBuf = ByteBuffer.wrap(message);

        // 接收到的总字节数
        int totalReceived = 0;
        // 当前接收到的字节数
        int curReceived = 0;

        // 这是个简单的例子，我们没有引入Selector，我们将服务端的行为设置为：接收到客户端的数据原样返回。
        // 因此在客户端判断是否完成数据的接收，可以判断接收的数据长度与客户端发出的数据长度是否一致
        while (totalReceived < message.length) {
            // 判断缓冲区中的数据是否已经全部写入
            // 在非阻塞模式下，写操作会立即返回，数据不一定会一次性发出，因此我们需要自己判断写入数据的总长度是否与待发送消息长度一致
            if (writeBuf.hasRemaining()) {
                clientChannel.write(writeBuf);
            }

            // 如果对端意外中断连接，则获取到的数据长度为-1。如果数据全部读取完毕，则read方法返回0
            if ((curReceived = clientChannel.read(readBuf)) == -1) {
                throw new SocketException("连接意外中断了");
            }

            totalReceived += curReceived;

            // 我们可以在这里做别的事情
        }

        System.out.println("收到来自服务端信息：" + new String(readBuf.array(), 0, totalReceived, "UTF-8"));
        clientChannel.close();
    }

}
