package com.voxldavid.chatter;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new ChatGUI();
        // System.out.println("Fertig");
    }
}