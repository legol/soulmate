package com.heaven.soulmate.longconn;

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
class TcpServer extends Thread
    implements ITcpServerDelegate
{
    private static final Logger LOGGER = Logger.getLogger(TcpServer.class);

    private Properties props;

    private Selector selector = null;
    private HashMap<SocketChannel, TcpClient> clientMap = new HashMap<SocketChannel, TcpClient>();

    private ITcpServerDelegate delegate = null;

    public TcpServer(ITcpServerDelegate delegate) {
        this.delegate = delegate;

        props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }
    }

    public void run() {
        LOGGER.info(String.format("start listening at: %s:%s\n", props.getProperty("ip"), props.getProperty("port")));

        try {
            selector = Selector.open();
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(props.getProperty("ip"), Integer.parseInt(props.getProperty("port"))));
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

                TcpClient client = new TcpClient(clientChannel, this);

                synchronized (clientMap){
                    clientMap.put(clientChannel, client);
                }

                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                //send(client, client.getClientId());
            }
            else{
                TcpClient client = null;
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

        TcpClient client = null;
        synchronized (clientMap){
            client = clientMap.get(channel);
        }

        if (client != null){ // if client is closed remotelly, this could happen.
            client.processRead();
        }

    }

    void processWrite(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();

        TcpClient client = null;
        synchronized (clientMap){
            client = clientMap.get(channel);
        }

        if (client != null){ // if client is closed remotelly, this could happen.
            client.processWrite();
        }
    }

    public void clientConnected(TcpClient client) {
        LOGGER.info(String.format("client:<%s> connected.\n", client.getClientId()));

        delegate.clientConnected(client);
    }

    public void clientDisconnected(TcpClient client) {
        LOGGER.info(String.format("client:<%s> disconnected\n", client.getClientId()));

        delegate.clientDisconnected(client);

        try {
            client.getChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (clientMap){
            clientMap.remove(client.getChannel());
        }
    }

    public void packetReceived(TcpClient client, TcpPacket packet) {
        LOGGER.info(String.format("packet:<%s>\n", packet.payload));

        delegate.packetReceived(client, packet);
    }

    public void send(TcpClient client, String payload) {
        client.send(payload);
        client.getChannel().keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
   }
}
