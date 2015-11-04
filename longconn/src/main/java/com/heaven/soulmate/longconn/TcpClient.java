package com.heaven.soulmate.longconn;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static java.util.UUID.randomUUID;

/**
 * Created by legol on 2015/11/3.
 */
public class TcpClient {

    private String clientId = randomUUID().toString();

    private ITcpServerDelegate delegate = null;
    private SocketChannel channel = null;

    private TcpPacket receivingPacket = null;

    public TcpClient(SocketChannel channel, ITcpServerDelegate delegate) {
        this.channel = channel;
        this.delegate = delegate;

        delegate.clientConnected(this);
    }

    void processRead() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate( 1024 * 10 );
            int bytesRead = channel.read(buffer);

            if (bytesRead > 0) {
                buffer.flip();
                buffer.position(0);
            }
            else{
                delegate.clientDisconnected(this);
                return;
            }

            int bytesConsumed = 0;
            int bytesToConsume = bytesRead;
            while (bytesToConsume > 0){
                if (receivingPacket == null) {
                    receivingPacket = new TcpPacket();
                }

                bytesConsumed = receivingPacket.append(buffer, bytesToConsume);
                if (bytesConsumed < bytesToConsume){
                    buffer = ByteBuffer.allocate(1024*10).put(buffer.array(), bytesConsumed, bytesToConsume - bytesConsumed);
                    buffer.flip();
                    buffer.position(0);
                }
                bytesToConsume -= bytesConsumed;

                if (receivingPacket.completed()){
                    delegate.packetReceived(this, receivingPacket);
                    receivingPacket = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            delegate.clientDisconnected(this);
        }
    }

    void processWrite(){

    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }
}
