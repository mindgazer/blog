package org.mindgazer.bio.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author mindgazer
 * @date 2020/01/17
 */
public class SimpleTcpClient {

    private static final int TIMEOUT = 8000;

    public static void main(String[] args) throws IOException {
        Socket client = new Socket();

        System.out.println("start connecting");
        client.connect(new InetSocketAddress("127.0.0.1", 8888), TIMEOUT);
        System.out.println("connection created");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF("This is Major Tom to Ground Control!");

        DataInputStream in = new DataInputStream(client.getInputStream());
        String response = in.readUTF();
        System.out.println("server said: " + response);

        in.close();
        out.close();
        client.close();
    }

}
