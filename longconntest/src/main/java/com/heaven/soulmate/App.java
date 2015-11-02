package com.heaven.soulmate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        try {
            Socket socket = new Socket("localhost", 7788);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String payload1 = "hello longconn!";

            out.writeInt(1);
            out.writeLong(payload1.length());
            out.writeBytes(payload1);

            String payload2 = "hahahahahahaha oh yeah!";

            out.writeInt(1);
            out.writeLong(payload2.length());
            out.writeBytes(payload2);

            sleep(1000);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
