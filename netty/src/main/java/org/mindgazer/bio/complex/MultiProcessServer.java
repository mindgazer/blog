package org.mindgazer.bio.complex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 一个支持对client的server示例
 *
 * @author mindgazer
 * @date 2020/01/17
 */
public class MultiProcessServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("waiting for the clients...");

        // 收到来自客户端的连接请求，交给Handler处理，然后继续监听等待
        while (true) {
            Socket client = server.accept();
            System.out.println("a client connected");
            new ClientHandler(client).start();
        }
    }

    /**
     * 负责处理每一个聊天者的会话
     */
    private static final class ClientHandler extends Thread {

        Socket client;

        ClientHandler(Socket client) throws IOException {
            this.client = client;
        }

        @Override
        public void run() {
            DataInputStream in = null;
            DataOutputStream out = null;
            try {
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                while (true) {
                    // 此方法会一直阻塞，直到client有数据发过来
                    String str = in.readUTF();
                    out.writeUTF("Copy that: " + str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    in.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
