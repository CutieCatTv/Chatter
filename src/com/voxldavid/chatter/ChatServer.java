package com.voxldavid.chatter;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatServer extends Server {
    int chatRoomIdCounter;
    ArrayList<Integer> unusedIds;
    HashMap<Integer, ArrayList<Integer>> loggedInUsers;
    HashMap<Integer, String> userIP;

    java.sql.Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:chats.db");
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public ChatServer() {
        super(1001);

        chatRoomIdCounter = 1;
        unusedIds = new ArrayList<Integer>();
        loggedInUsers = new HashMap<Integer, ArrayList<Integer>>();
        userIP = new HashMap<Integer, String>();

        String url = "jdbc:sqlite:chats.db";

        try (java.sql.Connection c = connect(); Statement stmt = c.createStatement()) {
            if (c != null) {
                DatabaseMetaData meta = c.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());

                String sql = "CREATE TABLE IF NOT EXISTS chatrooms (\n" + "  name text UNIQUE NOT NULL,\n"
                        + "  pass text,\n" + "  chatRoomID int UNIQUE NOT NULL\n" + ");";

                stmt.execute(sql);

                sql = "CREATE TABLE IF NOT EXISTS users (\n" + "  name text UNIQUE NOT NULL,\n"
                        + "  pass text NOT NULL,\n" + "  id int PRIMARY KEY UNIQUE NOT NULL" + ");";

                stmt.execute(sql);

                sql = "CREATE TABLE IF NOT EXISTS unused (\n" + "  id int UNIQUE NOT NULL\n" + ");";

                stmt.execute(sql);

                ResultSet rs;

                sql = "SELECT id FROM unused";

                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    unusedIds.add(rs.getInt("id"));
                }

                sql = "SELECT chatRoomID FROM chatrooms";

                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    loggedInUsers.put(rs.getInt("chatRoomID"), new ArrayList<Integer>());
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN CONSTRUCTOR: " + e.getMessage());
        }
    }

    public void createRoom(String name, String pass) {
        String sql = String.format("SELECT name FROM chatrooms WHERE name='%s';", name);

        try (var c = connect()) {
            Statement stmt = c.createStatement();

            if (stmt.executeQuery(sql).getFetchSize() == 0) {
                sql = "INSERT INTO chatrooms(name, pass, chatRoomID) VALUES(?,?,?);";

                PreparedStatement pstmt = c.prepareStatement(sql);

                int id = chatRoomIdCounter;
                chatRoomIdCounter++;

                pstmt.setString(1, name);
                pstmt.setString(2, pass);
                pstmt.setInt(3, id);
                pstmt.executeUpdate();

                pstmt.close();

                sql = "CREATE TABLE IF NOT EXISTS room" + id + " (\n" + "  source text NOT NULL,\n"
                        + "  time datetime NOT NULL,\n" + "  message text NOT NULL\n" + ");";

                c.createStatement().execute(sql);

                loggedInUsers.put(id, new ArrayList<Integer>());

                System.out.println("Created room " + name + " with pass: " + pass);
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN CREATE ROOM: " + e.getMessage());
        }
    }

    public void destroyRoom(String name, String pass) {

    }

    public void receiveChatMessage(String chatRoomName, String chatRoomPass, String source, String message) {

        java.sql.Date dt = java.sql.Date.valueOf(LocalDate.now());

        try (var c = connect()) {
            Statement stmt = c.createStatement();

            String sql = String.format("SELECT chatRoomID FROM chatrooms WHERE name='%s' AND pass='%s';", chatRoomName,
                    chatRoomPass);

            int chatRoomID = stmt.executeQuery(sql).getInt("chatRoomID");

            if (chatRoomID != 0) {
                sql = "INSERT INTO room" + chatRoomID + "(source, time, message) VALUES(?,?,?);";

                PreparedStatement pstmt = c.prepareStatement(sql);

                pstmt.setString(1, source);
                pstmt.setDate(2, dt);
                pstmt.setString(3, message);
                pstmt.executeUpdate();

                pstmt.close();

                for (Integer user : loggedInUsers.get(chatRoomID)) {

                    String[] sendStrings = userIP.get(user).split(";");

                    String ip = sendStrings[0];
                    int port = Integer.parseInt(sendStrings[1]);

                    send(ip, port, "message;" + source + ";" + dt.getTime() + ";" + message);
                }
            }

            System.out.println("Received message " + message + " from " + source + " for room " + chatRoomName);

        } catch (SQLException e) {
            System.out.println("ERROR IN RECEIVE MESSAGE: " + e.getMessage());
        }
    }

    public void loginUser(String ip, int port, String name, String pass) {
        try (var c = connect()) {
            String sql = String.format("SELECT * FROM users WHERE name='%s' AND pass='%s'", name, pass);

            Statement s = c.createStatement();

            var result = s.executeQuery(sql);

            if (result.next()) {
                userIP.put(result.getInt("id"), ip + ";" + port);
                send(ip, port, "loggedinuser;" + result.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN USER LOGIN: " + e.getMessage());
        }
    }

    @Override
    public void processMessage(String clientIp, int clientPort, String message) {
        String[] splitMessage = message.split(";");

        if (splitMessage[0].equals("create")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            createRoom(name, pass);
            return;
        }

        if (splitMessage[0].equals("login")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            loginUser(clientIp, clientPort, name, pass);
            return;
        }

        if (splitMessage[0].equals("message")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            String source = clientIp;
            String msg = splitMessage[3];
            receiveChatMessage(name, pass, source, msg);
            return;
        }
    }
}
