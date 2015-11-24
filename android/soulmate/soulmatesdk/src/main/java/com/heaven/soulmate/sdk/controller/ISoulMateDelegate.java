package com.heaven.soulmate.sdk.controller;

import com.heaven.soulmate.sdk.model.longconn.TcpClient;
import com.heaven.soulmate.sdk.model.longconn.TcpPacket;

/**
 * Created by ChenJie3 on 2015/11/24.
 */
public interface ISoulMateDelegate {
    void loginFailed();
    void packetReceived(TcpClient client, TcpPacket packet);
}
