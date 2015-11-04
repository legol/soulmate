package com.heaven.soulmate.longconn.network;

/**
 * Created by legol on 2015/11/4.
 */
public interface ITcpServerDelegate {
    void clientConnected(TcpClient client);
    void clientDisconnected(TcpClient client);
    void packetReceived(TcpClient client, TcpPacket packet);
}
