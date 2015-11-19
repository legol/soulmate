package com.heaven.soulmate.longconn.network;

/**
 * Created by legol on 2015/11/4.
 */
public interface ITcpServerDelegate {
    void clientConnected(TcpServer server, IncomingTcpClient client);
    void clientDisconnected(TcpServer server, IncomingTcpClient client);
    void packetReceived(TcpServer server, IncomingTcpClient client, TcpPacket packet);
}
