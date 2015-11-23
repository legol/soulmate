package com.heaven.soulmate.model.longconn;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by legol on 2015/11/4.
 */
public interface ITcpClientDelegate {
    void connectionFailure(TcpClient client); // can't connect to remove host
    void connected(TcpClient client);
    void connectionLost(TcpClient client);
    void packetReceived(TcpClient client, TcpPacket packet);
}
