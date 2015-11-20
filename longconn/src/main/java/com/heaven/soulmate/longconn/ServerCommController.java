package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.LoginStatusDao;
import com.heaven.soulmate.model.LongConnRegisterMessage;
import com.heaven.soulmate.model.LongConnMessage;
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

    private void processChatMsgPacket(TcpServer server, IncomingTcpClient client, LongConnMessage longconnMsg){
        ObjectMapper mapper = new ObjectMapper();

        // todo: deliver the msg
        //clientController.sendMessage(longconnMsg.getTargetUid(), longconnMsg.getPayload());

        LongConnMessage resultMsg = new LongConnMessage();
        resultMsg.setErrNo(0);
        resultMsg.setType(1);

        String resultMsgInJson = null;
        try {
            resultMsgInJson = mapper.writeValueAsString(resultMsg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        server.send(client, resultMsgInJson);
    }

    public void packetReceived(TcpServer server, IncomingTcpClient client, TcpPacket packet) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LongConnMessage longconnMsg = null;

            longconnMsg = mapper.readValue(packet.payload, LongConnMessage.class);

            switch (longconnMsg.getType()){
                case 2:{// chat msg
                    processChatMsgPacket(server, client, longconnMsg);
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
