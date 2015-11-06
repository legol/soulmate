package com.heaven.soulmate.longconn;

import com.heaven.soulmate.longconn.network.ITcpServerDelegate;
import com.heaven.soulmate.longconn.network.TcpClient;
import com.heaven.soulmate.longconn.network.TcpPacket;

/**
 * Created by chenjie3 on 2015/11/6.
 */
public class ClientCommController implements ITcpServerDelegate {
    public void clientConnected(TcpClient client) {

    }

    public void clientDisconnected(TcpClient client) {

    }

    public void packetReceived(TcpClient client, TcpPacket packet) {

    }
}
