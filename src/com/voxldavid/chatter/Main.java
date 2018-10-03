package com.voxldavid.chatter;

import javax.swing.UIManager;

/**
 * Startet einen Server und einen Client
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        StartServer.main(args);
        StartClient.main(args);
    }
}