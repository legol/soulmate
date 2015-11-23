package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.ITcpServerDelegate;
import com.heaven.soulmate.longconn.network.IncomingTcpClient;
import com.heaven.soulmate.longconn.network.TcpPacket;
import com.heaven.soulmate.longconn.network.TcpServer;
import com.heaven.soulmate.model.LoginStatusDao;
import com.heaven.soulmate.model.LongConnMessage;
import com.heaven.soulmate.model.LongConnRegisterMessage;
import com.heaven.soulmate.model.chat.ChatMessages;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class ClientCommController implements ITcpServerDelegate {
    private static final Logger LOGGER = Logger.getLogger(ClientCommController.class);

    private TcpServer server = null;

    private TreeMap<Long, IncomingTcpClient> clientMap = new TreeMap<Long, IncomingTcpClient>();

    public ClientCommController() {
        Properties props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }

        server = new TcpServer(this,  props.getProperty("ip"), Integer.parseInt(props.getProperty("port")));
        server.start();
    }

    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unregisterClient(IncomingTcpClient client){
        clientMap.remove(client.getUid());
    }

    public void registerClient(IncomingTcpClient client){
        clientMap.put(client.getUid(), client);
    }

    public boolean sendChatMsg(LongConnMessage longconnMsg){

        assert(longconnMsg.getType() == 2);

        ObjectMapper mapper = new ObjectMapper();
        ChatMessages chatMsg = null;
        try {
            chatMsg = mapper.readValue(longconnMsg.getPayload(), ChatMessages.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chatMsg == null){
            LOGGER.error(String.format("can't parse chat msg %s", longconnMsg.getPayload()));
            return false;
        }

        IncomingTcpClient tcpClient = clientMap.get(chatMsg.getTarget_uid());
        if (tcpClient == null) {
            LOGGER.info(String.format("uid=%d not connected. message can't be delivered.", chatMsg.getTarget_uid()));
            return false;
        }

        server.send(tcpClient, longconnMsg.getPayload());
        // when receive ack from client, update offline_msg db

        return true;
    }

    //////////////////////////////////////////////////////////////////
    // ITcpServerDelegate
    public void clientConnected(TcpServer server, IncomingTcpClient client) {
    }

    public void clientDisconnected(TcpServer server, IncomingTcpClient client) {
        LOGGER.info(String.format("client disconnected. uid=%d", client.getUid()));
        unregisterClient(client);
    }

    private void processRegisterPacket(TcpServer server, IncomingTcpClient client, LongConnMessage longconnMsg){
        assert(longconnMsg.getType() == 1);

        ObjectMapper mapper = new ObjectMapper();
        LongConnRegisterMessage longconnRegMsg = null;

        try {
            longconnRegMsg = mapper.readValue(longconnMsg.getPayload(), LongConnRegisterMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (longconnRegMsg == null){
            return;
        }

        // verify token
        if (!LoginStatusDao.sharedInstance().verifyToken(longconnRegMsg.getUid(), longconnRegMsg.getToken())){
            LOGGER.info(String.format("client token verification failed. uid=%d token=%s", longconnRegMsg.getUid(), longconnRegMsg.getToken()));
            return;
        }

        // save to map{uid->client}
        client.setUid(longconnRegMsg.getUid()); // when client disconnected, we'll use the uid
        registerClient(client);

        LOGGER.info(String.format("client connected. uid=%d", client.getUid()));
    }

    public void packetReceived(TcpServer server, IncomingTcpClient client, TcpPacket packet) {
        LOGGER.info("packet received from client.");

        try {
            ObjectMapper mapper = new ObjectMapper();
            LongConnMessage longconnMsg = null;

            longconnMsg = mapper.readValue(packet.payload, LongConnMessage.class);

            switch (longconnMsg.getType()){
                case 1:{
                    processRegisterPacket(server, client, longconnMsg);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error("unknown server message" + packet.payload);
            return;
        }
    }


}
