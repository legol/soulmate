package com.heaven.soulmate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
//
//        MainLoop mainloop = new MainLoop();
//        mainloop.start();
//        mainloop.join();

        try {

            Socket socket = new Socket("localhost", 7788);


            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            byte[] buff = new byte[1024*10];
            int protocol = in.readInt();
            int payloadSize = in.readInt();
            in.read(buff, 0, payloadSize);
            String payload = new String(buff, 0, payloadSize, StandardCharsets.UTF_8);

            String payload1 = "hello longconn!";

            out.writeInt(1);
            out.writeInt(payload1.length());
            out.writeBytes(payload1);

            String payload2 = "hahahahahahaha oh yeah!";

            out.writeInt(1);
            out.writeInt(payload2.length());
            out.writeBytes(payload2);

            sleep(1000);



        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
