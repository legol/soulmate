package com.heaven.soulmate.longconn;

import java.io.*;
import java.net.Socket;

import static java.util.UUID.randomUUID;


/**
 * Created by ChenJie3 on 2015/11/2.
 */
class Client extends Thread {

    private FrontGate fg = null;

    private String clientId = "";

    private Socket socket = null;
    private DataInputStream in = null;
    private OutputStreamWriter out = null;
    final private int bufferLen = 1024;
    final private int protocolSupported = 1;

    public Client(Socket socket, FrontGate fg) {
        this.socket = socket;
        this.fg = fg;
    }

    public boolean process(String payload) {
        return fg.process(this, payload);
    }

    public void clientDisconnected(){
        fg.clientDisconnected(this);
    }

    public void run() {
        byte inputBuffer[] = null;
        int bytesRead = 0;

        int protocol = 0;
        long payloadLen = 0L;

        try {
            in = new DataInputStream(this.socket.getInputStream());
            out = new OutputStreamWriter(this.socket.getOutputStream());

            while (true) {
                /*
                protocol:int
                payload_size:long
                payload:a string of size "payload_size"
                * */

                protocol = in.readInt();
                if (protocol != protocolSupported) {
                    socket.close();
                    break;
                }

                payloadLen = in.readLong();
                assert (payloadLen > 0);

                inputBuffer = new byte[(int) payloadLen];
                bytesRead = 0;
                while (bytesRead != payloadLen) {
                    bytesRead += in.read(inputBuffer, bytesRead, (int) (payloadLen - bytesRead));
                }

                String payload = new String(inputBuffer, 0, (int) payloadLen);
                if (!process(payload))
                {
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                this.socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        clientDisconnected();
    }

    public String getClientId() {
        if (clientId.isEmpty())
        {
            clientId = randomUUID().toString();
        }
        return clientId;
    }
}
