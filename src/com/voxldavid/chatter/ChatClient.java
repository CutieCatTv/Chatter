package com.voxldavid.chatter;

class ChatClient extends Client {
    Boolean loggedInRoom;
    String loginRoom;
    String loginRoomPass;

    Boolean loggedInUser;
    String userName;

    Interface userInterface;

    public ChatClient(Interface userInterface) {
        super("127.0.0.1", 1001);
        loggedInUser = false;
        loggedInRoom = false;
        this.userInterface = userInterface;
    }

    @Override
    public void processMessage(String message) {
        String[] parts = message.split(";");

        if (parts[0].equals("message")) {
            userInterface.receiveMessage(parts[1], parts[2], parts[3]);

        } else if(parts[0].equals("loggedinuser")) {
            userName = parts[1];
            loggedInUser = true;
            userInterface.userLoginSuccess(userName);
        } else if (parts[0].equals("loggedinroom")) {
            loginRoom = parts[1];
            loginRoomPass = parts[2];
            loggedInRoom = true;
            userInterface.loginSuccess(loginRoom);
        } else if (parts[0].equals("loggedout")) {
            userInterface.logoutSuccess();
        }
    }

    public void loginRoom(String name, String pass) {
        if (!loggedInRoom) {
            send("loginroom;" + name + ";" + pass);
        } else {
            userInterface.alreadyLoggedInRoom(loginRoom);
        }
    }

    public void loginUser(String name, String pass) {
        if (!loggedInUser) {
            send("login;" + name + ";" + pass);
        } else {
            userInterface.alreadyLoggedInUser(userName);
        }
    }

    public void registerUser(String name, String pass) {
        if (!loggedInUser) {
            send("register;" + name + ";" + pass);
        } else {
            userInterface.alreadyLoggedInUser(userName);
        }
    }

    public void logoutRoom() {
        send("logoutroom");
    }

    public void logoutUser() {
        send("logout");
    }

    public void sendMessage(String msg) {
        if (loggedInRoom) {
            send("message;" + loginRoom + ";" + loginRoomPass + ";" + msg);
        } else {
            userInterface.requestLoginRoom();
        }
    }
}