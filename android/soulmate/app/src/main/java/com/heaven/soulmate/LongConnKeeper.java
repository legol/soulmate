package com.heaven.soulmate;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.heaven.soulmate.model.longconn.ITcpClientDelegate;
import com.heaven.soulmate.model.longconn.TcpClient;
import com.heaven.soulmate.model.longconn.TcpPacket;

import java.util.logging.Logger;

/**
 * Created by ChenJie3 on 2015/11/23.
 */
public class LongConnKeeper
        implements ITcpClientDelegate{
    private String longconnHost;
    private int longconnPort;
    private int reconnectInterval = 3; // in seconds
    private ITcpClientDelegate tcpClientDelegate = null;

    private TcpClient tcpClient = null;

    public LongConnKeeper(ITcpClientDelegate tcpClientDelegate) {
        this.tcpClientDelegate = tcpClientDelegate;
    }

    public String getLongconnHost() {
        return longconnHost;
    }

    public int getLongconnPort() {
        return longconnPort;
    }

    public void setLongconnHost(String longconnHost) {
        this.longconnHost = longconnHost;
    }

    public void setLongconnPort(int longconnPort) {
        this.longconnPort = longconnPort;
    }

    public TcpClient getClient(){
        return tcpClient;
    }

    void connect(){
        if (tcpClient == null){
            Log.i("network", String.format("trying to connect to %s:%d", longconnHost, longconnPort));
            tcpClient = new TcpClient(this, longconnHost, longconnPort);
            tcpClient.start();
        }
    }

    @Override
    public void connectionFailure(TcpClient client) {
        Log.i("network", String.format("can't connect to %s:%d", longconnHost, longconnPort));

        tcpClientDelegate.connectionFailure(client);

        tcpClient = null;
        connect();
    }

    @Override
    public void connected(TcpClient client){
        Log.i("network", String.format("connected to %s:%d", longconnHost, longconnPort));

        tcpClientDelegate.connected(client);
    }

    @Override
    public void connectionLost(TcpClient client) {
        Log.i("network", String.format("connection lost from %s:%d", longconnHost, longconnPort));

        tcpClientDelegate.connectionLost(client);

        tcpClient = null;
        connect();
    }

    @Override
    public void packetReceived(TcpClient client, TcpPacket packet) {
        tcpClientDelegate.packetReceived(client, packet);
    }
}
