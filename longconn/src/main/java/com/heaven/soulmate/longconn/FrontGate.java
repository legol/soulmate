package com.heaven.soulmate.longconn;

import com.heaven.soulmate.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/11/2.
 */
class FrontGate extends Thread {

    private Properties props;

    public FrontGate() {
        props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }
    }

    public boolean process(Client client, String payload)
    {
        // payload is a json string
        System.out.println(payload);
        return true;
    }

    public void clientDisconnected(Client client) {
        System.out.println(String.format("client:%s disconnected", client.getClientId()));
    }

    public void run() {
        System.out.println(String.format("start listening at port:%s", props.getProperty("listen")));

        ServerSocket listener = null;
        try {
            listener = new ServerSocket(Integer.parseInt(props.getProperty("listen")));
            while (true) {
                Client client = new Client(listener.accept(), this);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
