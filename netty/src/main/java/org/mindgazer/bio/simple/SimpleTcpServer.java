package org.mindgazer.bio.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author mindgazer
 * @date 2020/01/17
 */
public class SimpleTcpServer {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8888);
            System.out.println("waiting for the clients...");
            Socket client = server.accept();
            System.out.println("client connected");
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String str = in.readUTF();
            System.out.println("client said: " + str);

            out.writeUTF("This is Ground Control to Major Tom!");
            out.close();
            in.close();
            server.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
