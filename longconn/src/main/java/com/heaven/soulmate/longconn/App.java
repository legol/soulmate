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

        ClientCommController clientController = new ClientCommController();
        ServerCommController serverController = new ServerCommController(clientController);

        clientController.join();
        serverController.join();
    }
}
