package com.voxldavid.chatter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 13.09.2018
 * @author
 */

public class Anmeldung extends JFrame {
    // Anfang Attribute
    private JLabel lBenutzername = new JLabel();
    private JLabel lPasswort1 = new JLabel();
    private JLabel lAnmeldung = new JLabel();
    private JButton bAnmelden = new JButton();
    private JButton bRegistrieren = new JButton();
    String b;
    String p1;
    private JTextField jTextField1 = new JTextField();
    private JPasswordField jPasswordField1 = new JPasswordField();
    ChatGUI gui;
	private JLabel ausgabe;
    // Ende Attribute

    public Anmeldung(ChatGUI c) {
        // Frame-Initialisierung
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 383;
        int frameHeight = 500;
        setSize(383, 305);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setTitle("Anmeldung");
        setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(null);
        gui = c;
        // Anfang Komponenten

        lBenutzername.setBounds(8, 48, 99, 41);
        lBenutzername.setText("Benutzername:");
        cp.add(lBenutzername);
        lPasswort1.setBounds(8, 96, 99, 41);
        lPasswort1.setText("Passwort:");
        cp.add(lPasswort1);
        lAnmeldung.setBounds(0, 8, 347, 33);
        lAnmeldung.setText("Anmeldung");
        lAnmeldung.setHorizontalAlignment(SwingConstants.CENTER);
        lAnmeldung.setFont(new Font("Dialog", Font.BOLD, 16));
        cp.add(lAnmeldung);
        bAnmelden.setBounds(112, 144, 249, 33);
        bAnmelden.setText("Anmelden");
        bAnmelden.setMargin(new Insets(2, 2, 2, 2));
        bAnmelden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bAnmelden_ActionPerformed(evt);
            }
        });
        cp.add(bAnmelden);
        bRegistrieren.setBounds(112, 184, 249, 33);
        bRegistrieren.setText("Registrieren");
        bRegistrieren.setMargin(new Insets(2, 2, 2, 2));
        bRegistrieren.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bRegistrieren_ActionPerformed(evt);
            }
        });
        cp.add(bRegistrieren);
        jTextField1.setBounds(112, 48, 249, 41);
        cp.add(jTextField1);
        jPasswordField1.setBounds(112, 96, 249, 41);
        cp.add(jPasswordField1);
        
        ausgabe = new JLabel("");
        ausgabe.setBounds(112, 228, 249, 33);
        getContentPane().add(ausgabe);
        // Ende Komponenten

        setVisible(true);
    } // end of public Anmeldung

    // Anfang Methoden

    public void bAnmelden_ActionPerformed(ActionEvent evt) {
        b = jTextField1.getText();
        p1 = new String(jPasswordField1.getPassword());
        if (b.equals("")) {
            if (p1.equals("")) {
                ausgabe.setText("Bitte Benutzername und Passwort eingeben!");
            } else {
                ausgabe.setText("Bitte Benutzername eingeben!");
            }
        } else {
            if (p1.equals("")) {
                ausgabe.setText("Bitte Passwort eingeben!");
            } else {
                ausgabe.setText("Benutzername oder Passwort falsch!");
            }
        }

        gui.client.loginUser(jTextField1.getText(), new String(jPasswordField1.getPassword()));
    } // end of bAnmelden_ActionPerformed

    public void bRegistrieren_ActionPerformed(ActionEvent evt) {
        String name = (String) JOptionPane.showInputDialog(this, "Benutzername", "Benutzername",
                JOptionPane.PLAIN_MESSAGE);
        if ((name == null) || (name.length() < 1))
            return;
        String pass = (String) JOptionPane.showInputDialog(this, "Passwort eingeben",
                "Passwort", JOptionPane.PLAIN_MESSAGE);
        if ((pass == null) || (pass.length() < 1))
            return;
        gui.client.registerUser(name, pass);
    } // end of bRegistrieren_ActionPerformed
} // end of class Anmeldung
