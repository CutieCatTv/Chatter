package com.voxldavid.chatter;

class StartServer {
    public static ChatServer server;

    public static void close() {
        StartServer.server.close();
        System.out.println("Server closed");
    }

    public static void main(String[] args) {
        StartServer.server = new ChatServer();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                StartServer.close();
            }
        }));
    }
}