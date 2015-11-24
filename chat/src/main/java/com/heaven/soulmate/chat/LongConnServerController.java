package com.heaven.soulmate.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.chat.model.LongConnMessage;
import com.heaven.soulmate.chat.model.LongConnServerInfo;
import com.heaven.soulmate.chat.model.ServerInfo;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class LongConnServerController {
    private static final Logger LOGGER = Logger.getLogger(LongConnServerController.class);
    private static LongConnServerInfo longConnServerInfo = null;

    private static LongConnServerController instance = null;

    public static LongConnServerController sharedInstance() {
        if(instance == null) {
            instance = new LongConnServerController();
        }
        return instance;
    }

    public LongConnServerController(){
        // read longconn server info
        if (longConnServerInfo == null){
            LOGGER.info("read long conn server info.");

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("longconn.json");

            ObjectMapper mapper = new ObjectMapper();
            try {
                longConnServerInfo = mapper.readValue(stream, LongConnServerInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerInfo serverByUid(long uid){
        LinkedList<ServerInfo> servers = new LinkedList<ServerInfo>();

        for (ServerInfo server:longConnServerInfo.info) {
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

    public Boolean sendMessage(long targetUid, String payload){
        ServerInfo longconnServer = LongConnServerController.sharedInstance().serverByUid(targetUid);
        if (longconnServer == null){
            return false;
        }

        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            socket = new Socket();
            InetSocketAddress addr = new InetSocketAddress(longconnServer.ip, longconnServer.portServer);
            socket.connect(addr, 3000);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            LongConnMessage longconnMsg = new LongConnMessage();
            longconnMsg.setType(2);
            longconnMsg.setErrNo(0);
            longconnMsg.setPayload(payload);

            // longconnMsg to json
            ObjectMapper mapper = new ObjectMapper();
            String messageInJson = null;
            try {
                messageInJson = mapper.writeValueAsString(longconnMsg);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }

            out.writeInt(1);
            out.writeInt(messageInJson.length());
            out.writeBytes(messageInJson);

            LOGGER.info(String.format("payload sent. longconn=%s:%d target_uid=%d", longconnServer.ip, longconnServer.portServer, targetUid));
        } catch(ConnectException e){
            e.printStackTrace();
            LOGGER.error(String.format("can't connect to longconn=%s:%d", longconnServer.ip, longconnServer.portServer));
            return false;
        }
        catch (SocketTimeoutException e){
            e.printStackTrace();
            LOGGER.error(String.format("can't connect to longconn=%s:%d", longconnServer.ip, longconnServer.portServer));
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(String.format("can't send payload. longconn=%s:%d target_uid=%d", longconnServer.ip, longconnServer.portServer, targetUid));
            return false;
        }

        return true;
    }
}
