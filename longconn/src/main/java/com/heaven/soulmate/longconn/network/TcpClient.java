package com.heaven.soulmate.longconn.network;

import com.sun.media.jfxmedia.logging.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import static java.util.UUID.randomUUID;

/**
 * Created by legol on 2015/11/3.
 */
public class TcpClient {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TcpClient.class);

    private String clientId = randomUUID().toString();

    private ITcpServerDelegate delegate = null;
    private SocketChannel channel = null;
    private TcpServer server = null;

    ByteBuffer remainingBuffer = null;
    int remainingBufferSize = 0;

    private TcpPacket receivingPacket = null;

    private LinkedList<String> outgoingPayload = new LinkedList<String>();

    public TcpClient(SocketChannel channel, TcpServer server) {
        this.channel = channel;
        this.server = server;

        server.clientConnected(this);
    }

    boolean processRead() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate( 1024 * 10 );
            int bytesRead = channel.read(buffer);

            if (bytesRead > 0) {
                buffer.flip();
                buffer.position(0);
            }
            else{
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
                    server.packetReceived(this, receivingPacket);
                    receivingPacket = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    boolean processWrite(){
        while (!outgoingPayload.isEmpty()){
            TcpPacket newPacket = new TcpPacket(outgoingPayload.getLast());
            try {
                channel.write(newPacket.toByteBuffer());
            } catch (IOException e) {
                e.printStackTrace();
                outgoingPayload.clear();
                return false;
            }

            outgoingPayload.removeLast();
        }

        return true;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public void send(String payload){
        outgoingPayload.addFirst(payload);

    }

}
