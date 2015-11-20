package com.heaven.soulmate.longconn.network;

import com.heaven.soulmate.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Properties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Created by ChenJie3 on 2015/11/2.
 */
public class TcpServer extends Thread
{
    private static final Logger LOGGER = Logger.getLogger(TcpServer.class);

    private String ip = null;
    private int port = 0;

    private Selector selector = null;
    private HashMap<SocketChannel, IncomingTcpClient> clientMap = new HashMap<SocketChannel, IncomingTcpClient>();

    private ITcpServerDelegate delegate = null;

    public TcpServer(ITcpServerDelegate delegate, String ip, int port) {
        this.delegate = delegate;
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        LOGGER.info(String.format("start listening at: %s:%d\n", ip, port));

        try {
            selector = Selector.open();
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(ip, port));
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                System.out.println("select");
                if (selector.select() <= 0) {
                    continue;
                }

                processSelectedKeys(selector.selectedKeys());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void processSelectedKeys(Set selectedKeys) throws IOException {
        Iterator iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            System.out.println("process selected keys");
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isAcceptable()) {
                ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                SocketChannel clientChannel = (SocketChannel) ssChannel.accept();

                IncomingTcpClient client = new IncomingTcpClient(clientChannel, this);

                synchronized (clientMap){
                    clientMap.put(clientChannel, client);
                }

                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                //send(client, client.getClientId());
            }
            else{
                IncomingTcpClient client = null;
                synchronized (clientMap){
                    client = clientMap.get(key.channel());
                }

                boolean clientValid = (client != null);
                if (clientValid && key.isReadable()) {
                    clientValid &= client.processRead();
                }

                if (clientValid && key.isWritable()){
                    clientValid &= client.processWrite();
                    if (clientValid){
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }

                if (!clientValid){
                    clientDisconnected(client);
                }
            }
        }
    }

    void processRead(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();

        IncomingTcpClient client = null;
        synchronized (clientMap){
            client = clientMap.get(channel);
        }

        if (client != null){ // if client is closed remotelly, this could happen.
            client.processRead();
        }

    }

    void processWrite(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();

        IncomingTcpClient client = null;
        synchronized (clientMap){
            client = clientMap.get(channel);
        }

        if (client != null){ // if client is closed remotelly, this could happen.
            client.processWrite();
        }
    }

    public void clientConnected(IncomingTcpClient client) {
        delegate.clientConnected(this, client);
    }

    public void clientDisconnected(IncomingTcpClient client) {
        delegate.clientDisconnected(this, client);

        try {
            client.getChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (clientMap){
            clientMap.remove(client.getChannel());
        }
    }

    public void packetReceived(IncomingTcpClient client, TcpPacket packet) {
        LOGGER.info(String.format("packet:<%s>\n", packet.payload));

        delegate.packetReceived(this, client, packet);
    }

    public void send(IncomingTcpClient client, String payload) {
        client.send(payload);
        client.getChannel().keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
   }
}
