package com.heaven.soulmate.longconn;

import com.heaven.soulmate.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ChenJie3 on 2015/11/2.
 */
class TcpServer extends Thread
    implements ITcpServerDelegate
{
    private Properties props;

    private Selector selector = null;
    private HashMap<SocketChannel, TcpClient> clientMap = new HashMap<SocketChannel, TcpClient>();

    public TcpServer() {
        props = Utils.readProperties("server.properties");
        if (props == null) {
            return;
        }
    }

    public void run() {
        System.out.printf("start listening at: %s:%s\n", props.getProperty("ip"), props.getProperty("port"));

        try {
            selector = Selector.open();
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(props.getProperty("ip"), Integer.parseInt(props.getProperty("port"))));
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
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
                clientChannel.register(selector, SelectionKey.OP_READ);
            }
            else if (key.isReadable()) {
                processRead(key);
                key.channel().register(selector, SelectionKey.OP_WRITE);
            }
            else if (key.isWritable()){
                processWrite(key);
                key.channel().register(selector, SelectionKey.OP_READ);
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
        System.out.printf("client:<%s> connected.\n", client.getClientId());
    }

    public void clientDisconnected(TcpClient client) {

        System.out.printf("client:<%s> disconnected\n", client.getClientId());
        synchronized (clientMap){
            //client.getChannel().keyFor(selector).cancel();;
            clientMap.remove(client.getChannel());
        }
    }

    public void packetReceived(TcpClient client, TcpPacket packet) {
        System.out.printf("packet:<%s>\n", packet.payload);
    }
}
