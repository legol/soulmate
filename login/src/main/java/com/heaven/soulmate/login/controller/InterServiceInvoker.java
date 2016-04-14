package com.heaven.soulmate.login.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.ServerSelector;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.login.model.BroadcastRequest;
import com.heaven.soulmate.login.model.ServerInfo;
import com.heaven.soulmate.login.model.ServerInfoList;
import org.apache.log4j.Logger;

/**
 * Created by ChenJie3 on 2016/4/14.
 */
public class InterServiceInvoker {
    private static final Logger LOGGER = Logger.getLogger(InterServiceInvoker.class);


    public static boolean broadcast(String message){
        ServerInfoList websocketServers = ServerSelector.sharedInstance().selectServersBy("websocket");
        BroadcastRequest broadcastRequest = new BroadcastRequest();
        broadcastRequest.message = message;

        ObjectMapper mapper = new ObjectMapper();
        String requestInJson = null;
        try {
            requestInJson = mapper.writeValueAsString(broadcastRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(String.format("unknown error happened while trying to broadcast"));
            return false;
        }

        for (ServerInfo websocket:websocketServers.info) {
            String response = Utils.httpPost(String.format("http://%s:%d/websocket/broadcast", websocket.ip, websocket.portServer), requestInJson);
            LOGGER.info(String.format("broadcast: response=%s", response));
        }

        return true;
    }
}
