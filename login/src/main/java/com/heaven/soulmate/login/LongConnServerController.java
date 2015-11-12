package com.heaven.soulmate.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.login.model.LongConnServerInfo;
import com.heaven.soulmate.login.model.ServerInfo;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
}
