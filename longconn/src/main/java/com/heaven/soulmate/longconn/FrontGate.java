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

    public void run() {
        System.out.println(String.format("start listening at port:%s", props.getProperty("listen")));

        ServerSocket listener = null;
        try {
            listener = new ServerSocket(Integer.parseInt(props.getProperty("listen")));
            while (true) {
                //new Handler(listener.accept()).start();
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
