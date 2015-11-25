package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.LongConnMessage;
import com.heaven.soulmate.model.chat.ChatRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by chenjie3 on 2015/11/4.
 */
public class ServerCommController implements ITcpServerDelegate{
    private static final Logger LOGGER = Logger.getLogger(ServerCommController.class);

    private TcpServer server = null;

    private ClientCommController clientController = null;

    public ServerCommController(ClientCommController clientController) {
        this.clientController = clientController;

        Properties props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }

        server = new TcpServer(this,  props.getProperty("ipServerComm"), Integer.parseInt(props.getProperty("portServerComm")));
        server.start();
    }

    public void join() {
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    // ITcpServerDelegate
    public void clientConnected(TcpServer server, IncomingTcpClient client) {

    }

    public void clientDisconnected(TcpServer server, IncomingTcpClient client) {
    }

    private boolean processChatMsgPacket(TcpServer server, IncomingTcpClient client, LongConnMessage longconnMsg){
        boolean delivered = false;
        try {
            ObjectMapper mapper = new ObjectMapper();

            // deliver the msg
            if (longconnMsg.getType() == 2) { // chat msg
                ChatRequest chatMsg = mapper.readValue(longconnMsg.getPayload(), ChatRequest.class);
                delivered = clientController.sendChatMsg(longconnMsg);

                LOGGER.info(String.format("deliver chat msg to client. uid_from=%d uid_to=%d message_id=%d delivered=%b", chatMsg.getUid(), chatMsg.getTarget_uid(), chatMsg.getMessageId(), delivered));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return delivered;
        } catch (IOException e) {
            e.printStackTrace();
            return delivered;
        }

        return delivered;
    }

    public void packetReceived(TcpServer server, IncomingTcpClient client, TcpPacket packet) {
        LOGGER.info("packet received from server.");

        try {
            ObjectMapper mapper = new ObjectMapper();
            LongConnMessage longconnMsg = null;

            longconnMsg = mapper.readValue(packet.payload, LongConnMessage.class);

            switch (longconnMsg.getType()){
                case 2:{// chat msg
                    processChatMsgPacket(server, client, longconnMsg);
                    break;
                }
                default:{
                    LOGGER.error("unknown server message:" + packet.payload);
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error("unknown server message:" + packet.payload);
        }
    }
}
