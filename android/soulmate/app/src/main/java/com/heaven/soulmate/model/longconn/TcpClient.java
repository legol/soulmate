package com.heaven.soulmate.model.longconn;

import com.fasterxml.jackson.core.JsonProcessingException;

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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

/**
 * Created by ChenJie3 on 2015/11/2.
 */
public class TcpClient extends Thread
{
    private String hostAddress = null;
    private int hostPort = 0;

    private Selector selector = null;
    SocketChannel socketChannel = null;

    private ITcpClientDelegate delegate = null;

    private ByteBuffer remainingBuffer = null;
    private int remainingBufferSize = 0;
    private TcpPacket receivingPacket = null;
    private LinkedList<String> outgoingPayload = new LinkedList<String>();


    public TcpClient(ITcpClientDelegate delegate, String hostAddress, int hostPort) {
        this.delegate = delegate;
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
    }

    public void run() {
        try {
            selector = Selector.open();

            // Create a non-blocking socket channel
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            // Kick off connection establishment
            socketChannel.connect(new InetSocketAddress(this.hostAddress, this.hostPort));

            while (true) {
                System.out.println("select");
                if (selector.select() <= 0) {
                    continue;
                }

                if (!processSelectedKeys(selector.selectedKeys())){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean processSelectedKeys(Set selectedKeys) throws IOException {
        boolean valid;

        Iterator iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
            valid = true;

            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();
            if (key.isConnectable()) {
                socketChannel.finishConnect();
                socketChannel.register(selector, SelectionKey.OP_READ);

                valid &= connected();
            }
            else{
                if (valid && key.isReadable()) {
                    valid &= processRead();
                }

                if (valid && key.isWritable()){
                    valid &= processWrite();
                    key.interestOps(SelectionKey.OP_READ);
                }
            }

            if (!valid){
                return valid;
            }
        }

        return true;
    }

    private boolean processRead(){
        try {
            ByteBuffer buffer = ByteBuffer.allocate( 1024 * 10 );
            int bytesRead = socketChannel.read(buffer);

            if (bytesRead > 0) {
                buffer.flip();
                buffer.position(0);
            }
            else{
                connectionLost();
                return false;
            }

            if (remainingBuffer != null){
                buffer = ByteBuffer.allocate(1024*20).put(remainingBuffer.array(), 0, remainingBufferSize).put(buffer.array(), 0, bytesRead);
                buffer.flip();
                buffer.position(0);

                remainingBuffer = null;
                remainingBufferSize = 0;
            }

            int bytesConsumed = 0;
            int bytesToConsume = bytesRead;
            while (bytesToConsume > 0){
                System.out.println("consume");
                if (receivingPacket == null) {
                    receivingPacket = new TcpPacket();
                }

                bytesConsumed = receivingPacket.append(buffer, bytesToConsume);
                if (bytesConsumed == 0 && bytesToConsume != 0) {

                    remainingBuffer = ByteBuffer.allocate(1024*10).put(buffer.array(), 0, bytesToConsume);
                    remainingBufferSize = bytesToConsume;
                    remainingBuffer.flip();

                    break;
                }

                if (bytesConsumed < bytesToConsume){
                    buffer = ByteBuffer.allocate(1024*10).put(buffer.array(), bytesConsumed, bytesToConsume - bytesConsumed);
                    buffer.flip();
                    buffer.position(0);
                }
                bytesToConsume -= bytesConsumed;

                if (receivingPacket.completed()){
                    packetReceived(receivingPacket);
                    receivingPacket = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            connectionLost();
            return false;
        }

        return true;
    }

    private boolean processWrite(){
        while (!outgoingPayload.isEmpty()){
            TcpPacket newPacket = new TcpPacket(outgoingPayload.getLast());
            try {
                socketChannel.write(newPacket.toByteBuffer());
            } catch (IOException e) {
                e.printStackTrace();
                outgoingPayload.clear();
                return false;
            }

            outgoingPayload.removeLast();
        }

        return true;
    }

    public boolean connected(){
        try {
            delegate.connected(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return true;
    }
    public void connectionLost(){delegate.connectionLost(this);}

    public void packetReceived(TcpPacket packet) {
        delegate.packetReceived(this, packet);
    }

    public void send(String payload) {
        outgoingPayload.addFirst(payload);
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
   }
}
