package com.heaven.soulmate.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.login.model.ServerInfo;
import com.heaven.soulmate.login.model.ServerInfoList;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class ServerSelector {
    private static final Logger LOGGER = Logger.getLogger(ServerSelector.class);
    private static ServerSelector instance = null;

    ServerInfoList serverInfoList = null;

    public static ServerSelector sharedInstance() {
        if(instance == null) {
            instance = new ServerSelector();
        }
        return instance;
    }

    public ServerSelector(){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();;
        InputStream stream = null;
        ObjectMapper mapper = new ObjectMapper();

        // read server info
        LOGGER.info("read server info.");
        stream = loader.getResourceAsStream("servers.json");
        try {
            serverInfoList = mapper.readValue(stream, ServerInfoList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerInfoList selectServerBy(String role, long uid){
        ServerInfoList selectedServers = new ServerInfoList();
        selectedServers.info = new LinkedList<ServerInfo>();

        for (ServerInfo server:serverInfoList.info) {
            if (role.compareToIgnoreCase(server.role) == 0 && server.uidLow <= uid && uid < server.uidHigh){
                selectedServers.info.add(server);
            }
        }

        if (selectedServers.info.size() == 0){
            LOGGER.error(String.format("no server is found for:%d", uid));
            return null;
        }

        return selectedServers;
    }
}