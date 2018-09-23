package com.voxldavid.chatter;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 17.09.2018
 * @author 
 */

public class Registrierung extends JFrame {
  // Anfang Attribute
  private JLabel jLabel1 = new JLabel();
  // Ende Attribute
  
  public Registrierung() { 
    // Frame-Initialisierung
    super();
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 387; 
    int frameHeight = 300;
    setSize(frameWidth, frameHeight);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setTitle("Registrierung");
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    
    jLabel1.setBounds(8, 8, 331, 41);
    jLabel1.setText("text");
    cp.add(jLabel1);
    // Ende Komponenten
    
    setVisible(true);
  } // end of public Registrierung
  
  // Anfang Methoden
  
  public static void main(String[] args) {
    new Registrierung();
  } // end of main
  
  // Ende Methoden
} // end of class Registrierung

