package com.heaven.soulmate.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.websocket.model.LoginModelDAO;
import com.heaven.soulmate.websocket.model.LoginResult;
import com.heaven.soulmate.websocket.model.MessageAck;
import org.apache.log4j.Logger;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ChenJie3 on 2016/2/17.
 */
@ServerEndpoint(value="/chat_room", configurator = SpringConfigurator.class)
public class WebSocketServerEndPoint{

    private static final Logger LOGGER = Logger.getLogger(WebSocketServerEndPoint.class);


    public WebSocketServerEndPoint() {
    }

    /**
     * Callback hook for Connection open events.
     *
     * This method will be invoked when a client requests for a
     * WebSocket connection.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        LOGGER.info(String.format("onopen id:<%s>", userSession.getId()));
    }

    /**
     * Callback hook for Connection close events.
     *
     * This method will be invoked when a client closes a WebSocket
     * connection.
     *
     * @param userSession the userSession which is opened.
     */
    @OnClose
    public void onClose(Session userSession) {
        LOGGER.info(String.format("onclose id:<%s>", userSession.getId()));
    }

    /**
     * Callback hook for Message Events.
     *
     * This method will be invoked when a client send a message.
     *
     * @param message The text message
     * @param userSession The session of the client
     */
    @OnMessage
    public void onMessage(String message, Session userSession) {
        LOGGER.info(String.format("message received:<%s>", message));

        try {
            if (!authentication(message)){
                LOGGER.error(String.format("can't auth incoming message. <%s>", message));
                userSession.close();
                return;
            }

            if(!processMessage(message, userSession)){
                LOGGER.error(String.format("can't process message. <%s>", message));
                userSession.close();
                return;
            }

            // broadcast
//            for (Session session : userSession.getOpenSessions()) {
//                if (session.isOpen())
//                    session.getAsyncRemote().sendText(message);
//            }
        } catch (IOException e) {
            LOGGER.error(String.format("unknown error caused by message <%s>", message));
            e.printStackTrace();
        }
    }

    private boolean authentication(String message){
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> messageParsed = null;
        try {
            messageParsed = mapper.readValue(message, typeRef);
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error(String.format("can't parse incoming message. <%s>", message));
            return false;
        }

        long uid = Long.parseLong((String)messageParsed.get("uid"));
        String token = (String)messageParsed.get("token");
        LoginResult lr = LoginModelDAO.sharedInstance().websocketLogin(uid, token);
        if (lr.errno != 0){
            LOGGER.error(String.format("unauthorized uid <%d> with token <%s>", uid, token));
            return false;
        }

        return true;
    }

    private boolean processMessage(String message, Session userSession){
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> messageParsed = null;
        try {
            messageParsed = mapper.readValue(message, typeRef);
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error(String.format("can't parse incoming message. <%s>", message));
            return false;
        }

        MessageAck ack = new MessageAck();
        String messageInString = null;
        ack.msgid = ((Integer)messageParsed.get("msgid")).longValue();
        try {
            messageInString = mapper.writeValueAsString(ack);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error(String.format("can't assemble outgoing message."));
            return false;
        }

        if(userSession.isOpen()){
            userSession.getAsyncRemote().sendText(messageInString);
        }

        return true;
    }
}
