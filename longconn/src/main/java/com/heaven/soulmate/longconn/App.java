package com.heaven.soulmate.longconn;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ){

        LongConnHolder longconn = new LongConnHolder();

        TcpServer server = new TcpServer(longconn);
        server.start();

        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
