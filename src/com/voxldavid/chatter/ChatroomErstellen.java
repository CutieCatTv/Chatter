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

public class ChatroomErstellen extends JFrame {
  // Anfang Attribute
  private JLabel lChatroomerstellen = new JLabel();
  private JLabel lChatroomName = new JLabel();
  private JLabel lChatroomPasswort = new JLabel();
  private JTextField jTextField1 = new JTextField();
  private JTextField jTextField2 = new JTextField();
  private JButton bChatroomerstellen = new JButton();
  boolean pub;
  String n;
  String p;
  private JToggleButton ppublic = new JToggleButton();
  private JToggleButton pprivate = new JToggleButton();
  private JTextArea ausgabe = new JTextArea("");
    private JScrollPane ausgabeScrollPane = new JScrollPane(ausgabe);
  // Ende Attribute
  
  public ChatroomErstellen() { 
    // Frame-Initialisierung
    super();
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    int frameWidth = 370; 
    int frameHeight = 322;
    setSize(frameWidth, frameHeight);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (d.width - getSize().width) / 2;
    int y = (d.height - getSize().height) / 2;
    setLocation(x, y);
    setTitle("ChatroomErstellen");
    setResizable(false);
    Container cp = getContentPane();
    cp.setLayout(null);
    // Anfang Komponenten
    
    lChatroomerstellen.setBounds(8, 8, 339, 41);
    lChatroomerstellen.setText("Chatroom erstellen");
    lChatroomerstellen.setHorizontalAlignment(SwingConstants.CENTER);
    lChatroomerstellen.setFont(new Font("Dialog", Font.BOLD, 16));
    cp.add(lChatroomerstellen);
    lChatroomName.setBounds(8, 88, 115, 41);
    lChatroomName.setText("Chatroom Name:");
    cp.add(lChatroomName);
    lChatroomPasswort.setBounds(8, 136, 118, 41);
    lChatroomPasswort.setText("Chatroom Passwort");
    cp.add(lChatroomPasswort);
    jTextField1.setBounds(136, 88, 209, 41);
    cp.add(jTextField1);
    jTextField2.setBounds(136, 136, 209, 41);
    cp.add(jTextField2);
    bChatroomerstellen.setBounds(136, 184, 209, 33);
    bChatroomerstellen.setText("Chatroom erstellen");
    bChatroomerstellen.setMargin(new Insets(2, 2, 2, 2));
    bChatroomerstellen.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        bChatroomerstellen_ActionPerformed(evt);
      }
    });
    cp.add(bChatroomerstellen);
    ppublic.setBounds(136, 56, 97, 25);
    ppublic.setText("public");
    ppublic.setMargin(new Insets(2, 2, 2, 2));
    ppublic.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        ppublic_ActionPerformed(evt);
      }
    });
    cp.add(ppublic);
    pprivate.setBounds(248, 56, 97, 25);
    pprivate.setText("private");
    pprivate.setMargin(new Insets(2, 2, 2, 2));
    pprivate.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent evt) { 
        pprivate_ActionPerformed(evt);
      }
    });
    cp.add(pprivate);
    ausgabeScrollPane.setBounds(136, 224, 209, 49);
    ausgabe.setEditable(false);
    cp.add(ausgabeScrollPane);
    // Ende Komponenten
    
    setVisible(true);
  } // end of public ChatroomErstellen
  
  // Anfang Methoden
  
  public static void main(String[] args) {
    new ChatroomErstellen();
  } // end of main
  
  public void bChatroomerstellen_ActionPerformed(ActionEvent evt) {
    /*client.chatroomErstellen(jTextField1.getText(),jTextField2.getText());*/
    n = jTextField1.getText();
    p = jTextField2.getText();
    if (ppublic.isSelected()||pprivate.isSelected())  {
      if ( n.equals( "" ) )  {
        if (p.equals( "")) {
          ausgabe.setText("Bitte Name und Passwort eingeben!");
        } 
        else {
          ausgabe.setText("Bitte Name eingeben!");
        }
      } 
      else {
        if (p.equals( "")) {
          ausgabe.setText("Bitte Passwort eingeben!");
        }
        else {
            ausgabe.setText("Chatroom wird erstellt...");
            /*client.chatroomErstellen(jTextField1.getText(),jTextField2.getText());*/
            dispose();
        } 
      } 
    } 
    else {
      if ( n.equals( "" ) )  {
        if (p.equals( "")) {
          ausgabe.setText("Bitte public oder private auswählen! \nBitte Name und Passwort eingeben!");
        } 
        else {
          ausgabe.setText("Bitte public oder private auswählen! \nBitte Name eingeben!");
        }
      } 
      else {
        if (p.equals( "")) {
          ausgabe.setText("Bitte public oder private auswählen! \nBitte Passwort eingeben!");
        }
        else {
          ausgabe.setText("Bitte public oder private auswählen!");
        } 
      } 
    } // end of if-else
  } // end of bChatroomerstellen_ActionPerformed

  public void ppublic_ActionPerformed(ActionEvent evt) {
    pub = true;
    pprivate.setSelected(false);
  } // end of ppublic_ActionPerformed

  public void pprivate_ActionPerformed(ActionEvent evt) {
    pub = false;
    ppublic.setSelected(false);
  } // end of pprivate_ActionPerformed

  // Ende Methoden
} // end of class ChatroomErstellen

