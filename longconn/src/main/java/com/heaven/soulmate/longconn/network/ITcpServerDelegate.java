package com.heaven.soulmate.longconn.network;

/**
 * Created by legol on 2015/11/4.
 */
public interface ITcpServerDelegate {
    void clientConnected(TcpServer server, TcpClient client);
    void clientDisconnected(TcpServer server, TcpClient client);
    void packetReceived(TcpServer server, TcpClient client, TcpPacket packet);
}
