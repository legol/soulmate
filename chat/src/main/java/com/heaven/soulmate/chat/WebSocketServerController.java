package com.heaven.soulmate.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.chat.model.LongConnMessage;
import com.heaven.soulmate.chat.model.LongConnServerInfo;
import com.heaven.soulmate.chat.model.ServerInfo;
import com.heaven.soulmate.chat.model.WebSocketServerInfo;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class WebSocketServerController {
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerController.class);
    private static WebSocketServerInfo websocketServerInfo = null;

    private static WebSocketServerController instance = null;

    public static WebSocketServerController sharedInstance() {
        if(instance == null) {
            instance = new WebSocketServerController();
        }
        return instance;
    }

    public WebSocketServerController(){
        // read longconn server info
        if (websocketServerInfo == null){
            LOGGER.info("read websocket server info.");

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("websocket.json");

            ObjectMapper mapper = new ObjectMapper();
            try {
                websocketServerInfo = mapper.readValue(stream, WebSocketServerInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerInfo serverByUid(long uid){
        LinkedList<ServerInfo> servers = new LinkedList<ServerInfo>();

        for (ServerInfo server:websocketServerInfo.info) {
            if (server.uidLow <= uid && uid < server.uidHigh){
                servers.addFirst(server);
            }
        }

        int pickedServerIdx = ThreadLocalRandom.current().nextInt(0, servers.size());
        for (int i = 0; i < servers.size(); i++){
            if (i == pickedServerIdx){
                LOGGER.error(String.format("server:<%s:%d> for uid:%d", servers.getFirst().ip, servers.getFirst().portClient, uid));
                return servers.getFirst();
            }

            servers.removeFirst();
        }

        LOGGER.error(String.format("no server is found for:%d", uid));
        return null;
    }

    public Boolean sendMessage(long targetUid, int type, String payload){

        return true;
    }
}
