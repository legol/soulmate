package com.heaven.soulmate.longconn.network;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static java.lang.Integer.min;

/**
 * Created by legol on 2015/11/3.
 */
public class TcpPacket {
    private boolean protocolRead = false;
    private boolean payloadSizeRead = false;
    private boolean payloadRead = false;

    /* every packet follows below format:
    * protocol:int
    * payloadSize:int
    * payload:utf-8 string, not zero terminated*/
    public int protocol = 0;
    public int payloadSize = 0;
    public String payload = "";

    private int payloadSizeRemaining = 0;
    final private int protocolSupported = 1;

    public TcpPacket() {
    }

    public TcpPacket(String payload) {
        this.protocol = protocolSupported;
        this.payloadSize = payload.length();
        this.payload = payload;
    }

    ByteBuffer toByteBuffer(){
        ByteBuffer buffer = ByteBuffer.allocate(1024*10);

        buffer.putInt(this.protocol);
        buffer.putInt(this.payloadSize);

        byte[] bytes = this.payload.getBytes( Charset.forName("UTF-8" ));
        buffer.put(bytes);

        buffer.flip();
        buffer.position(0);

        return buffer;
    }

    // return bytesConsumed
    public int append(ByteBuffer buffer, int bytesToAppend) {
        int position = 0;
        byte[] bufferArray = buffer.array();

        if (!protocolRead){
            if (bytesToAppend >= 4){
                protocol = buffer.getInt();
                protocolRead = true;
                buffer.position(position + 4);
                position += 4;

                bytesToAppend -= 4;
            }
        }

        if (protocolRead && !payloadSizeRead){
            if (bytesToAppend >= 4) {
                payloadSize = buffer.getInt();
                payloadSizeRemaining = payloadSize;
                payloadSizeRead = true;
                buffer.position(position + 4);
                position += 4;

                bytesToAppend -= 4;
            }
        }

        if (protocolRead && payloadSizeRead && !payloadRead){
            if (payloadSizeRemaining > 0){
                if (bytesToAppend > 0){
                    int payloadSizeToAppend = min(buffer.remaining(), payloadSizeRemaining);

                    String remainingPayload = new String(bufferArray, position, payloadSizeToAppend);
                    payload += remainingPayload;

                    payloadSizeRemaining -= payloadSizeToAppend;
                    buffer.position(position + payloadSizeToAppend);
                    position += payloadSizeToAppend;

                    bytesToAppend -= payloadSizeToAppend;
                }

                if (payloadSizeRemaining == 0){
                    payloadRead = true;
                }
            }
            else{ // == 0
                payloadRead = true;
            }
        }

        return position;
    }

    public boolean completed() {
        return (protocolRead && payloadSizeRead && payloadRead);
    }
}
