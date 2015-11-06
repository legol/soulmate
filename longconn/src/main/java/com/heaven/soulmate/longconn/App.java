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
            String testJson = "{\"typehaha\":1,\"content\":\"aabbcc\"}";

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

        ServerCommController serverCommController = new ServerCommController();
        TcpServer serverComm = new TcpServer(serverCommController,  props.getProperty("ipServerComm"), Integer.parseInt(props.getProperty("portServerComm")));
        serverComm.start();

        ClientCommController clientCommController = new ClientCommController();
        TcpServer clientComm = new TcpServer(clientCommController,  props.getProperty("ip"), Integer.parseInt(props.getProperty("port")));
        clientComm.start();


        try {
            serverComm.join();
            clientComm.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
