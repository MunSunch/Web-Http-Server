package com.munsun.serversocket.app;

public class ServerMain {
    private static final int DEFAULT_COUNT_THREAD = 64;
    public static void main(String[] args) {
        Server server = new Server(DEFAULT_COUNT_THREAD, new HttpParser());
        server.start(9999);
    }
}
