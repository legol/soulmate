package com.heaven.soulmate.longconn;

import com.heaven.soulmate.longconn.network.ITcpServerDelegate;
import com.heaven.soulmate.longconn.network.TcpClient;
import com.heaven.soulmate.longconn.network.TcpPacket;
import com.heaven.soulmate.longconn.network.TcpServer;
import org.apache.log4j.Logger;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class ClientCommController implements ITcpServerDelegate {
    private static final Logger LOGGER = Logger.getLogger(ClientCommController.class);

    private static ClientCommController instance = null;

    public static ClientCommController sharedInstance() {
        if(instance == null) {
            instance = new ClientCommController();
        }
        return instance;
    }

    public void sendMessage(long uid, String payload){
        LOGGER.info(String.format("send message to uid:%d payload:%s", uid, payload));

        // todo: send message to the client

    }

    //////////////////////////////////////////////////////////////////
    // ITcpServerDelegate
    public void clientConnected(TcpServer server, TcpClient client) {

    }

    public void clientDisconnected(TcpServer server, TcpClient client) {

    }

    public void packetReceived(TcpServer server, TcpClient client, TcpPacket packet) {

    }


}
