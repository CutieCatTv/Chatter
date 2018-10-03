package com.voxldavid.chatter;

class StartClient {
    public static ChatGUI client;

    public static void close() {
        StartClient.client.client.close();
        System.out.println("Client closed");
    }

    public static void main(String[] args) {
        StartClient.client = new ChatGUI();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                StartClient.close();
            }
        }));
    }
}