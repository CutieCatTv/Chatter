package com.voxldavid.chatter;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

import javax.swing.*;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 06.09.2018
 * @author
 */

public class ChatGUI extends JFrame implements Interface {

    // Anfang Attribute
    ChatClient client;
    private JTextArea jTextArea1 = new JTextArea("");
    private JScrollPane jTextArea1ScrollPane = new JScrollPane(jTextArea1);
    private JTextArea jTextArea2 = new JTextArea("");
    private JScrollPane jTextArea2ScrollPane = new JScrollPane(jTextArea2);
    private JButton bSenden = new JButton();
    private JList<String> jList1 = new JList<String>();
    private DefaultListModel<String> jList1Model = new DefaultListModel<String>();
    private JScrollPane jList1ScrollPane = new JScrollPane(jList1);
    private JButton jButton1 = new JButton();
    private JButton jButton2 = new JButton();

    Anmeldung anmeldung = new Anmeldung(this);
    // Anmeldung anmeldung = new Anmeldung(this);
    // Ende Attribute

    public ChatGUI() {
        // Frame-Initialisierung
        super();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 872;
        int frameHeight = 543;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setTitle("ChatGUI");
        setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(null);
        // Anfang Komponenten

        jTextArea2ScrollPane.setBounds(32, 432, 537, 65);
        cp.add(jTextArea2ScrollPane);
        jTextArea1ScrollPane.setBounds(32, 64, 617, 361);
        jTextArea1.setEditable(false);
        cp.add(jTextArea1ScrollPane);
        cp.add(jTextArea2ScrollPane);
        bSenden.setBounds(576, 432, 73, 65);
        bSenden.setText("Senden");
        bSenden.setMargin(new Insets(2, 2, 2, 2));
        bSenden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bSenden_ActionPerformed(evt);
            }
        });
        cp.add(bSenden);
        jList1.setModel(jList1Model);
        jList1ScrollPane.setBounds(656, 64, 161, 361);
        cp.add(jList1ScrollPane);
        jButton1.setBounds(656, 464, 161, 33);
        jButton1.setText("Neuen Chatroom erstellen");
        jButton1.setMargin(new Insets(2, 2, 2, 2));
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1_ActionPerformed(evt);
            }
        });
        cp.add(jButton1);
        jButton2.setBounds(656, 432, 161, 33);
        jButton2.setText("Chatroom beitreten");
        jButton2.setMargin(new Insets(2, 2, 2, 2));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2_ActionPerformed(evt);
            }
        });
        cp.add(jButton2);
        // Ende Komponenten
        setVisible(false);

        Boolean connected = false;
        while (!connected) {
            if (client != null) {
                client.close();
            }
            String ip = (String) JOptionPane.showInputDialog(this, "Enter Server address (IPv4)",
                    "Enter Server address", JOptionPane.PLAIN_MESSAGE);
            if ((ip == null) || (ip.length() < 1))
                continue;
            client = new ChatClient(this, ip.trim(), 8080);
            connected = client.istVerbunden();
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    } // end of public ChatGUI

    // Anfang Methoden

    public void requestLogin() {

    }

    public void alreadyLoggedIn(String roomName) {

    }

    public void logoutSuccess() {
        jButton2.setText("Chatroom beitreten");
        jTextArea1.setText("");
    }

    public void receiveMessage(String msg, String source, String time) {
        jTextArea1.setText(jTextArea1.getText() + "\n<" + source + ", " + new Date(time).toGMTString() + "> " + msg);
    }

    public void bSenden_ActionPerformed(ActionEvent evt) {
        client.sendMessage(jTextArea2.getText());
    } // end of bSenden_ActionPerformed

    public void jButton1_ActionPerformed(ActionEvent evt) {
        String name = (String) JOptionPane.showInputDialog(this, "Raumname eingeben", "Chatroom Name",
                JOptionPane.PLAIN_MESSAGE);
        if ((name == null) || (name.length() < 1))
            return;
        String pass = (String) JOptionPane.showInputDialog(this, "Raumpasswort eingeben (leer für keins)",
                "Chatroom Passwort", JOptionPane.PLAIN_MESSAGE);
        if ((pass == null) || (pass.length() < 1))
            return;
        client.createRoom(name, pass);
    } // end of jButton1_ActionPerformed

    public void jButton2_ActionPerformed(ActionEvent evt) {
        if (client.loggedInRoom) {
            client.logoutRoom();
        } else {
            if (jList1.isSelectionEmpty())
                return;

            if (jList1.getSelectedValue().substring(jList1.getSelectedValue().length() - 1).equals("-")) {
                String s = (String) JOptionPane.showInputDialog(this, "Passwort eingeben", "Passwort",
                        JOptionPane.PLAIN_MESSAGE);
                if ((s == null) || (s.length() < 1))
                    return;
                client.loginRoom(jList1.getSelectedValue().substring(0, jList1.getSelectedValue().length() - 1), s);
            } else {
                client.loginRoom(jList1.getSelectedValue().substring(0, jList1.getSelectedValue().length() - 1), "");
            }
        }
    } // end of jButton2_ActionPerformed

    @Override
    public void loginSuccess(String roomName) {
        jButton2.setText("Ausloggen (Raum)");
    }

    @Override
    public void requestLoginRoom() {

    }

    @Override
    public void alreadyLoggedInRoom(String roomName) {

    }

    @Override
    public void alreadyLoggedInUser(String roomName) {

    }

    @Override
    public void userLoginSuccess(String userName) {
        anmeldung.setEnabled(false);
        anmeldung.setVisible(false);
        setVisible(true);
    }

    @Override
    public void updateRoomList(String[] rooms) {
        jList1Model.clear();
        for (String s : rooms) {
            jList1Model.addElement(s);
        }
        revalidate();
    }

    // Ende Methoden
} // end of class ChatGUI
