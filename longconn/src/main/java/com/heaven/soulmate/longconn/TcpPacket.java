package com.heaven.soulmate.longconn;

import java.nio.ByteBuffer;

import static java.lang.Integer.min;

/**
 * Created by legol on 2015/11/3.
 */
public class TcpPacket {
    private boolean protocolRead = false;
    private boolean payloadSizeRead = false;
    private boolean payloadRead = false;

    public int protocol = 0;
    public int payloadSize = 0;
    public int payloadSizeRemaining = 0;
    public String payload = "";

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

        if (!payloadSizeRead){
            if (bytesToAppend >= 4) {
                payloadSize = buffer.getInt();
                payloadSizeRemaining = payloadSize;
                payloadSizeRead = true;
                buffer.position(position + 4);
                position += 4;

                bytesToAppend -= 4;
            }
        }

        if (!payloadRead){
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
        return (protocol != 0) && (payload.length() == payloadSize || payloadSize == 0);
    }
}
