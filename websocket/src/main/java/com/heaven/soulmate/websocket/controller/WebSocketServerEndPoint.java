package com.heaven.soulmate.websocket.controller;

import org.apache.log4j.Logger;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
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
        System.out.println("New request received. Id: " + userSession.getId());
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
        System.out.println("Connection closed. Id: " + userSession.getId());
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

        for (Session session : userSession.getOpenSessions()) {
            if (session.isOpen())
                session.getAsyncRemote().sendText(message);
        }
    }
}
