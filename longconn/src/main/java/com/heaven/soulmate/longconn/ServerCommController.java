package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.ServerMessage;
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

                    clientController.sendMessage(payloadMsg.getTargetUid(), payloadMsg.getPayload());

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
