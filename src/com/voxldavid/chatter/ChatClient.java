package com.voxldavid.chatter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

class ChatClient extends Client {
    Boolean loggedInRoom;
    String loginRoom;
    String loginRoomPass;

    Boolean loggedInUser;
    String userName;

    Interface userInterface;

    public ChatClient(Interface userInterface, String ip, int port) {
        super(ip, port);
        loggedInUser = false;
        loggedInRoom = false;
        this.userInterface = userInterface;
    }

    public ChatClient() {
        super("127.0.0.1", 1002);
        loggedInUser = false;
        loggedInRoom = false;
        this.userInterface = new ClientInterface();
    }

    @Override
    public void processMessage(String message) {
        String[] parts = message.split(";");

        if (parts[0].equals("message")) {
            Date d = new Date();
            d.setTime(Long.parseLong(parts[2]));
            userInterface.receiveMessage(parts[3], parts[1], DateFormat.getDateTimeInstance().format(d));
            System.out.println(parts[1] + ";" + parts[2] + ";" + parts[3]);
        } else if (parts[0].equals("loggedinuser")) {
            userName = parts[1];
            loggedInUser = true;
            ArrayList<String> rooms = new ArrayList<String>();
            for (int i = 2; i < parts.length; i++) {
                rooms.add(parts[i]);
            }
            userInterface.userLoginSuccess(userName);
            System.out.println("login user success");
            userInterface.updateRoomList(rooms.toArray(new String[0]));
        } else if (parts[0].equals("loggedinroom")) {
            loginRoom = parts[1];
            loginRoomPass = parts.length > 2 ? parts[2] : "";
            loggedInRoom = true;
            userInterface.loginSuccess(loginRoom);
            System.out.println("login room success");
        } else if (parts[0].equals("loggedout")) {
            loggedInRoom = false;
            userInterface.logoutSuccess();
            System.out.println("logout success");
        } else if (parts[0].equals("invalid")) {
            System.out.println("invalid user or pass");
        } else if (parts[0].equals("rooms")) {
            ArrayList<String> rooms = new ArrayList<String>();
            for (int i = 1; i < parts.length; i++) {
                rooms.add(parts[i]);
            }
            System.out.println("Received new Rooms");
            userInterface.updateRoomList(rooms.toArray(new String[0]));
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

    public void createRoom(String name, String pass) {
        send("create;" + name + ";" + pass);
    }

    public void sendMessage(String msg) {
        if (loggedInRoom) {
            send("message;" + loginRoom + ";" + loginRoomPass + ";" + userName + ";" + msg);
        } else {
            userInterface.requestLoginRoom();
        }
    }
}