package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.ServerMessage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by chenjie3 on 2015/11/4.
 */
public class ServerCommController implements ITcpServerDelegate{
    private static final Logger LOGGER = Logger.getLogger(ServerCommController.class);

    private static ServerCommController instance = null;

    public static ServerCommController sharedInstance() {
        if(instance == null) {
            instance = new ServerCommController();
        }
        return instance;
    }

    public void clientConnected(TcpServer server, TcpClient client) {

    }

    public void clientDisconnected(TcpServer server, TcpClient client) {

    }

    public void packetReceived(TcpServer server, TcpClient client, TcpPacket packet) {
        ObjectMapper mapper = new ObjectMapper();
        ServerMessage payloadMsg = null;
        try {
            payloadMsg = mapper.readValue(packet.payload, ServerMessage.class);

            switch (payloadMsg.getType()){
                case 1:{// chat msg
                    ClientCommController.sharedInstance().sendMessage(payloadMsg.getTargetUid(), payloadMsg.getPayload());

                    ServerMessage resultMsg = new ServerMessage();
                    resultMsg.setErrNo(0);
                    resultMsg.setType(1);

                    String resultMsgInJson = mapper.writeValueAsString(resultMsg);

                    server.send(client, resultMsgInJson);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error("unknown server message" + packet.payload);
            return;
        }
    }
}
