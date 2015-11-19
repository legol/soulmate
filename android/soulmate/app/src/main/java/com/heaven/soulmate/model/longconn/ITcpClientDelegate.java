package com.heaven.soulmate.model.longconn;

/**
 * Created by legol on 2015/11/4.
 */
public interface ITcpClientDelegate {
    void serverDisconnected(TcpClient client);
    void packetReceived(TcpClient client, TcpPacket packet);
}
