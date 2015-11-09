package com.heaven.soulmate.longconn;

/**
 * Hello world!
 *
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.Payload;

import java.io.IOException;
import java.util.Properties;

public class App
{
    public static void main( String[] args ){

        try {
            String testJson = "{\"type\":1,\"content\":\"aabbcc\"}";

            ObjectMapper mapper = new ObjectMapper();
            Payload obj = mapper.readValue(testJson, Payload.class);
            String jsonInString = mapper.writeValueAsString(obj);

            System.out.println(jsonInString);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }

        TcpServer serverComm = new TcpServer(ServerCommController.sharedInstance(),  props.getProperty("ipServerComm"), Integer.parseInt(props.getProperty("portServerComm")));
        serverComm.start();

        TcpServer clientComm = new TcpServer(ClientCommController.sharedInstance(),  props.getProperty("ip"), Integer.parseInt(props.getProperty("port")));
        clientComm.start();


        try {
            serverComm.join();
            clientComm.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
