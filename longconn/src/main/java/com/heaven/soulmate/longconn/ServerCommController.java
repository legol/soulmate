package com.heaven.soulmate.longconn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.*;
import com.heaven.soulmate.model.LoginStatusDao;
import com.heaven.soulmate.model.LongConnRegisterMessage;
import com.heaven.soulmate.model.LongConnMessage;
import com.heaven.soulmate.model.chat.ChatMessages;
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
        try {
            boolean delivered = false;
            ObjectMapper mapper = new ObjectMapper();

            // deliver the msg
            if (longconnMsg.getType() == 2) { // chat msg
                ChatMessages chatMsg = mapper.readValue(longconnMsg.getPayload(), ChatMessages.class);
                delivered = clientController.sendChatMsg(longconnMsg);
            }

            LongConnMessage resultMsg = new LongConnMessage();
            resultMsg.setType(2);
            if (delivered){
                resultMsg.setErrNo(0);
            }else{
                resultMsg.setErrNo(-1);
                resultMsg.setErrMsg("can't deliver msg to the client");
            }
            String resultMsgInJson = mapper.writeValueAsString(resultMsg);
            server.send(client, resultMsgInJson);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
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

            }
        } catch (IOException e) {
            e.printStackTrace();

            LOGGER.error("unknown server message" + packet.payload);
            return;
        }
    }
}
