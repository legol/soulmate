package com.heaven.soulmate.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.websocket.model.LoginModelDAO;
import com.heaven.soulmate.websocket.model.LoginResult;
import com.heaven.soulmate.websocket.model.MessageAck;
import org.apache.log4j.Logger;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by ChenJie3 on 2016/2/17.
 */
@ServerEndpoint(value="/chat_room", configurator = SpringConfigurator.class)
public class WebSocketServerEndPoint{

    public static Map<Long, Set<Session>> uidToSessions = Collections.synchronizedMap(new HashMap<Long, Set<Session>>());
    public static Map<String, Long> sessionIdToUid = Collections.synchronizedMap(new HashMap<String, Long>());

    private static final Logger LOGGER = Logger.getLogger(WebSocketServerEndPoint.class);

    public boolean sendMsg(long uid, String message){
        if (!uidToSessions.containsKey(uid)) {
            LOGGER.error(String.format("uid=%d is not connected to me.", uid));
            return false;
        }

        Set<Session> sessionSet = uidToSessions.get(uid);
        for (Session session : sessionSet) {
            if (session.isOpen()){
                session.getAsyncRemote().sendText(message);
            }
        }

        LOGGER.info(String.format("send msg:%s to uid=%d", message, uid));

        return true;
    }

    public void broadcast(String message){
        LOGGER.info(String.format("broadcasting msg:%s", message));

        for (Set<Session> sessionSet : uidToSessions.values()) {
            for (Session session : sessionSet) {
                if (session.isOpen()){
                    LOGGER.info(String.format("broadcasting to uid=%d", sessionIdToUid.get(session.getId()).longValue()));
                    session.getAsyncRemote().sendText(message);
                }
            }
        }
        LOGGER.info(String.format("broadcast complete"));

    }

    @OnOpen
    public void onOpen(Session userSession) {
        LOGGER.info(String.format("onopen id:<%s>", userSession.getId()));
    }

    private void cleanup(Session userSession){
        if(sessionIdToUid.containsKey(userSession.getId())){
            long uid = sessionIdToUid.get(userSession.getId()).longValue();
            LoginModelDAO.sharedInstance().websocketLogout(uid, userSession.getId());
        }

        removeFromMap(userSession);
    }

    @OnClose
    public void onClose(Session userSession) {
        LOGGER.info(String.format("onclose id:<%s>", userSession.getId()));

        cleanup(userSession);
    }

    @OnError
    public void onError(Session userSession, Throwable t){ // javdoc says that method need to have mandatory Throwable parameter so on deploy module throws server exception
        LOGGER.info(String.format("onerror id:<%s>", userSession.getId()));

        cleanup(userSession);
    }

    @OnMessage
    public void onMessage(String message, Session userSession) {
         LOGGER.info(String.format("message received:<%s>", message));

        try {
            long uid = -1;
            if ((uid = authentication(message, userSession.getId())) <= 0){
                LOGGER.error(String.format("can't auth incoming message. <%s>", message));
                userSession.close();
                return;
            }

            addToMap(uid, userSession);

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

    private long authentication(String message, String websocket_session_id){
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> messageParsed = null;
        try {
            messageParsed = mapper.readValue(message, typeRef);
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error(String.format("can't parse incoming message. <%s>", message));
            return -1;
        }

        long uid = Long.parseLong((String)messageParsed.get("uid"));
        String token = (String)messageParsed.get("token");
        LoginResult lr = LoginModelDAO.sharedInstance().websocketLogin(uid, token, websocket_session_id);
        if (lr.errno != 0){
            LOGGER.error(String.format("unauthorized uid <%d> with token <%s>", uid, token));
            return -1;
        }

        return uid;
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

    // map maintainence
    private void addToMap(long uid, Session session){
        if (!uidToSessions.containsKey(uid)){
            uidToSessions.put(uid, Collections.synchronizedSet(new HashSet<Session>()));
        }

        LOGGER.info(String.format("map uid=%d to session_id=%s", uid, session.getId()));

        uidToSessions.get(uid).add(session);
        sessionIdToUid.put(session.getId(), uid);
    }

    private void removeFromMap(Session session){
        if (sessionIdToUid.containsKey(session.getId())){
            long uid = sessionIdToUid.get(session.getId()).longValue();
            Set<Session> sessionSet = uidToSessions.get(uid);
            sessionSet.remove(session);

            if (sessionSet.isEmpty()){
                sessionIdToUid.remove(uid);
            }
        }
    }


}
