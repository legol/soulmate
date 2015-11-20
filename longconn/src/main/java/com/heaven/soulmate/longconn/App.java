package com.heaven.soulmate.longconn;

/**
 * Hello world!
 *
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.model.LongConnMessage;

import java.io.IOException;

public class App
{
    public static void main( String[] args ){

        try {
            String testJson = "{\"type\":1,\"payload\":\"aabbcc\"}";

            ObjectMapper mapper = new ObjectMapper();
            LongConnMessage obj = mapper.readValue(testJson, LongConnMessage.class);
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
