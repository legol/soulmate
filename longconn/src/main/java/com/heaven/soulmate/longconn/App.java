package com.heaven.soulmate.longconn;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ){
        System.out.println( "Hello World!" );

        TcpServer server = new TcpServer();
        server.start();

        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
