package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerGUI extends JFrame implements ActionListener{

    // GUI Part
    private JTextField inputField;
    private JTextArea chatArea;
    private JTextArea fileArea;
    private JTextArea connection;
    private JTextArea clientInfo;
    private ServerBackground sb;

    private JButton sendButton;

    ServerGUI() {

        sb = new ServerBackground();
        setTitle("SERVER");

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        fileArea = new JTextArea();
        fileArea.setEditable(false);
        inputField = new JTextField();
        inputField.setEnabled(false);

        connection = new JTextArea("Not connected");
        connection.setEditable(false);

        JTextArea keyInfo = new JTextArea("There is no key & Cannot transfer to client");
        keyInfo.setEditable(false);

        clientInfo = new JTextArea("There is no client's PublicKey");
        /*
        Buttons, and actionListeners
         */

        JButton genButton = new JButton("Key generation");
        JButton loadButton = new JButton("Load from a file");
        JButton saveButton = new JButton("Save into a file");
        sendButton = new JButton("Send public key");
        sendButton.setEnabled(false);

        genButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sb.setKeyPair();
                    keyInfo.setText("Key generation is complete! Plz send public key to client");
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sb.sendPublicKey();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Desktop desktop = null;
                String path = System.getProperty("user.dir");
                if(Desktop.isDesktopSupported()){
                    desktop = Desktop.getDesktop();
                }

                try{
                    desktop.open(new File(path));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        /*
        End of Button Part
         */
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText() + "\n";
                chatArea.append("Server : " + message);
                inputField.setText("");
                sb.sendMessage(message);
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatArea);
        JScrollPane scrollPane2 = new JScrollPane(fileArea);

        setVisible(true);
        setSize(600, 700);
        setLayout(null);

        add(scrollPane);
        add(scrollPane2);
        add(inputField);
        add(connection);
        add(keyInfo); add(clientInfo);
        add(genButton); add(loadButton); add(saveButton); add(sendButton);


        scrollPane.setBounds(100, 0, 400, 200);
        inputField.setBounds(100, 200, 400, 30);
        connection.setBounds(10, 250, 600, 20);

        keyInfo.setBounds(10, 280, 600, 20);
        clientInfo.setBounds(210, 330, 400, 30);
        genButton.setBounds(10, 300, 200, 30);
        loadButton.setBounds(210, 300, 200, 30);
        saveButton.setBounds(410, 300, 200, 30);
        sendButton.setBounds(10, 330, 200, 30);

        scrollPane2.setBounds(100, 370, 400, 200);


        sb.setGUI(this);
        sb.setting();
    }


    public void appendMSG(String message){
        chatArea.append("Client : " + message);
    }

    public void enableInputField(){
        inputField.setEnabled(true);
        clientInfo.setText("Successful in receiving Client's symmetric key \n You can send your message!");
    }

    public void connect(){
        sendButton.setEnabled(true);
        connection.setText("Client is connected ! ><");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}
