package org.mindgazer.bio.complex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 一个能够持续与Server通信的客户端示例
 *
 * @author mindgazer
 * @date 2020/01/17
 */
public class ContinuousClient {

    private static final int TIMEOUT = 8000;

    public static void main(String[] args) throws IOException, InterruptedException {
        // 启动3个Client
        for (int i = 0; i < 3; ++i) {
            new AClient().start();
        }
        // do something
    }

    private static final class AClient extends Thread {

        @Override
        public void run() {
            Socket client = new Socket();
            String clientName = getName();

            try {
                client.connect(new InetSocketAddress("127.0.0.1", 8888), TIMEOUT);
                DataInputStream in = new DataInputStream(client.getInputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                // 无限时间与Server保持通信
                for (int i = 0; ; i++) {
                    out.writeUTF(String.format("This is %s speaking for %s times", clientName, i));
                    String response = in.readUTF();
                    System.out.println(String.format("[%s]response from the server: %s", clientName, response));
                    Thread.sleep(2000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
