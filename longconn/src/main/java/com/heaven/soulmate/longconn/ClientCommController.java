package com.heaven.soulmate.longconn;

import com.heaven.soulmate.Utils;
import com.heaven.soulmate.longconn.network.ITcpServerDelegate;
import com.heaven.soulmate.longconn.network.IncomingTcpClient;
import com.heaven.soulmate.longconn.network.TcpPacket;
import com.heaven.soulmate.longconn.network.TcpServer;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class ClientCommController implements ITcpServerDelegate {
    private static final Logger LOGGER = Logger.getLogger(ClientCommController.class);

    private TcpServer server = null;

    TreeMap<Long, IncomingTcpClient> clientMap = new TreeMap<Long, IncomingTcpClient>();

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

    public void sendMessage(long uid, String payload){
        LOGGER.info(String.format("send message to uid:%d payload:%s", uid, payload));

        // todo: send message to the client

        // if message is sent, update db with delivered = 1

    }

    //////////////////////////////////////////////////////////////////
    // ITcpServerDelegate
    public void clientConnected(TcpServer server, IncomingTcpClient client) {
    }

    public void clientDisconnected(TcpServer server, IncomingTcpClient client) {

    }

    public void packetReceived(TcpServer server, IncomingTcpClient client, TcpPacket packet) {

    }


}
