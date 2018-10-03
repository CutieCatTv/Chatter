package com.voxldavid.chatter;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Server Klasse, welche eine Verbindung zur Datenbank (chats.db) aufbaut und
 * sämtliche Verbindungen von Clients und Chaträumen, etc verwaltet
 */
public class ChatServer extends Server {
    int chatRoomIdCounter;
    int userIdCounter;
    ArrayList<Integer> unusedIds;
    HashMap<Integer, ArrayList<Integer>> loggedInUsers;
    HashMap<Integer, String> userIP;

    // Verbindung zur Datenbank aufbauen
    java.sql.Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:chats.db");
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public ChatServer() {
        super(8080);

        chatRoomIdCounter = 1;
        userIdCounter = 1;
        unusedIds = new ArrayList<Integer>();
        loggedInUsers = new HashMap<Integer, ArrayList<Integer>>();
        userIP = new HashMap<Integer, String>();

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
                    chatRoomIdCounter = chatRoomIdCounter <= rs.getInt("chatRoomID") ? rs.getInt("chatRoomID") + 1
                            : chatRoomIdCounter;
                }

                sql = "SELECT id FROM users";

                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    userIdCounter = userIdCounter <= rs.getInt("id") ? rs.getInt("id") + 1 : userIdCounter;
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

                Statement roomStatement = c.createStatement();
                sql = "SELECT * FROM chatrooms";
                var roomResult = roomStatement.executeQuery(sql);
                String msgString = "rooms";
                while (roomResult.next()) {
                    msgString += ";" + roomResult.getString("name")
                            + (roomResult.getString("pass").equals("") ? "+" : "-");
                }
                sendToAll(msgString);
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN CREATE ROOM: " + e.getMessage());
        }
    }

    public void destroyRoom(String name, String pass) {

    }

    public void receiveChatMessage(String chatRoomName, String chatRoomPass, String source, String message) {
        var l = LocalDateTime.now();
        java.sql.Date dt = new java.sql.Date(l.toInstant(ZoneOffset.UTC).toEpochMilli());

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
                String msgString = "loggedinuser;" + result.getString("name");
                Statement roomStatement = c.createStatement();
                sql = "SELECT * FROM chatrooms";
                var roomResult = roomStatement.executeQuery(sql);
                while (roomResult.next()) {
                    msgString += ";" + roomResult.getString("name")
                            + (roomResult.getString("pass").equals("") ? "+" : "-");
                }
                send(ip, port, msgString);
            } else {
                send(ip, port, "invalid");
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN USER LOGIN: " + e.getMessage());
        }
    }

    /**
     * Erstellt einen neuen Benutzer in der Datenbank und loggt den Client in jenen
     * ein
     * 
     * @param ip Client ip
     * @param port Client port
     * @param name Benutzername vom neuen Benutzer
     * @param pass Passwort vom neuen Benutzer
     */
    public void registerUser(String ip, int port, String name, String pass) {
        try (var c = connect()) {
            String sql = String.format("SELECT * FROM users WHERE name='%s' AND pass='%s'", name, pass);

            Statement s = c.createStatement();

            var result = s.executeQuery(sql);

            if (!result.next()) {
                sql = "INSERT INTO users(name, pass, id) VALUES(?,?,?);";

                PreparedStatement pstmt = c.prepareStatement(sql);

                int id = userIdCounter;

                pstmt.setString(1, name);
                pstmt.setString(2, pass);
                pstmt.setInt(3, id);
                pstmt.executeUpdate();

                pstmt.close();

                String msgString = "loggedinuser;" + name;
                sql = "SELECT * FROM chatrooms";
                Statement roomStatement = c.createStatement();
                var roomResult = roomStatement.executeQuery(sql);
                while (roomResult.next()) {
                    msgString += ";" + roomResult.getString("name")
                            + (roomResult.getString("pass").equals("") ? "+" : "-");
                }
                send(ip, port, msgString);
            } else {
                send(ip, port, "invalid");
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN USER LOGIN: " + e.getMessage());
        }
    }

    public void loginRoom(String ip, int port, String name, String pass) {
        var l = LocalDateTime.now();
        java.sql.Date dt = new java.sql.Date(l.toInstant(ZoneOffset.UTC).toEpochMilli());

        try (var c = connect()) {
            int user = 0;
            for (var i : userIP.keySet()) {
                if (userIP.get(i).equals(ip + ";" + port)) {
                    user = i;
                }
            }

            if (user == 0) {
                return;
            }

            String sql = String.format("SELECT * FROM chatrooms WHERE name='%s'", name, pass);

            var result = c.createStatement().executeQuery(sql);

            sql = String.format("SELECT name FROM users WHERE id='%s'", Integer.toString(user));

            var userResult = c.createStatement().executeQuery(sql);

            userResult.next();

            String uname = userResult.getString("name");

            if (result.next()) {
                if (result.getString("pass").equals(pass)) {
                    loggedInUsers.get(result.getInt("chatRoomID")).add(user);
                    send(ip, port, "loggedinroom;" + result.getString("name") + ";" + result.getString("pass"));
                    for (Integer u : loggedInUsers.get(result.getInt("chatRoomID"))) {

                        String[] sendStrings = userIP.get(u).split(";");

                        String uip = sendStrings[0];
                        int uport = Integer.parseInt(sendStrings[1]);

                        send(uip, uport, "message;[server];" + dt.getTime() + ";" + uname + " joined the room");
                    }
                } else {
                    send(ip, port, "invalid");
                }
            } else {
                send(ip, port, "invalid");
            }
        } catch (SQLException e) {
            System.out.println("ERROR IN ROOM LOGIN: " + e.getMessage());
        }
    }

    public void logoutRoom(String clientIp, int clientPort) {
        String ipp = clientIp + ";" + clientPort;
        int user = 0;

        for (var i : userIP.keySet()) {
            if (userIP.get(i).equals(ipp)) {
                user = i;
                break;
            }
        }

        if (user == 0)
            return;

        int room = -5;
        int roomindex = -5;

        for (Integer i : loggedInUsers.keySet()) {
            for (int j = 0; j < loggedInUsers.get(i).size(); j++) {
                if (loggedInUsers.get(i).get(j) == user) {
                    room = i;
                    roomindex = j;
                    break;
                }
            }
        }

        if (room == -5 || roomindex == -5)
            return;

        loggedInUsers.get(room).remove(roomindex);
        send(clientIp, clientPort, "loggedout");
    }

    @Override
    public void processMessage(String clientIp, int clientPort, String message) {
        String[] splitMessage = message.split(";");

        if (splitMessage[0].equals("create")) {
            String name = splitMessage[1];
            String pass = splitMessage.length > 2 ? splitMessage[2] : "";
            createRoom(name, pass);
            return;
        }

        if (splitMessage[0].equals("login")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            loginUser(clientIp, clientPort, name, pass);
            return;
        }

        if (splitMessage[0].equals("register")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            registerUser(clientIp, clientPort, name, pass);
            return;
        }

        if (splitMessage[0].equals("loginroom")) {
            String name = splitMessage[1];
            String pass = "";
            if (splitMessage.length > 2)
                pass = splitMessage[2];
            loginRoom(clientIp, clientPort, name, pass);
            return;
        }

        if (splitMessage[0].equals("message")) {
            String name = splitMessage[1];
            String pass = splitMessage[2];
            String source = splitMessage[3];
            String msg = splitMessage[4];
            receiveChatMessage(name, pass, source, msg);
            return;
        }

        if (splitMessage[0].equals("logoutroom")) {
            logoutRoom(clientIp, clientPort);
        }
    }
}
