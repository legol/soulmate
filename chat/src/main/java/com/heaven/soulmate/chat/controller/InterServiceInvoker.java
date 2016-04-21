package com.heaven.soulmate.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.ServerSelector;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.chat.model.WebsocketBroadcast;
import com.heaven.soulmate.chat.model.ServerInfo;
import com.heaven.soulmate.chat.model.ServerInfoList;
import com.heaven.soulmate.chat.model.WebsocketNotification;
import org.apache.log4j.Logger;

/**
 * Created by ChenJie3 on 2016/4/14.
 */
public class InterServiceInvoker {
    private static final Logger LOGGER = Logger.getLogger(InterServiceInvoker.class);


    public static boolean broadcast(String message){
        ServerInfoList websocketServers = ServerSelector.sharedInstance().selectServersBy("websocket");
        WebsocketBroadcast broadcastRequest = new WebsocketBroadcast();
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

    public static boolean notifyClientsChanged(){
        String onlineClientsChangedNotification = "";
        WebsocketNotification notification = new WebsocketNotification();
        notification.type = "online_clients_changed";

        try {
            ObjectMapper mapper = new ObjectMapper();
            onlineClientsChangedNotification = mapper.writeValueAsString(notification);
            InterServiceInvoker.broadcast(onlineClientsChangedNotification);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(String.format("unknown error"));
            return false;
        }

        return true;
    }
}
