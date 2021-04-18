package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientGUI extends JFrame implements ActionListener {

    // GUI Part
    private JTextField inputField;
    private JTextArea chatArea;
    private JTextArea connection;
    private JTextArea clientInfo;
    private ClientBackground cb;

    private JButton sendButton;

    ClientGUI() {

        cb = new ClientBackground();
        setTitle("CLIENT");

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        inputField = new JTextField();
        inputField.setEnabled(false);

        connection = new JTextArea("Not connected");
        connection.setEditable(false);

        JTextArea keyInfo = new JTextArea("There is no key & Cannot transfer to server");
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
                    cb.setKeyPair();
                    keyInfo.setText("Key generation is complete! Plz send public key to server");
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cb.sendPublicKey();
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
                chatArea.append("Client : " + message);
                inputField.setText("");
                cb.sendMessage(message);
            }
        });

        JScrollPane scrollPane = new JScrollPane(chatArea);

        setVisible(true);
        setSize(600, 600);
        setLayout(null);

        add(scrollPane);
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


        cb.setGUI(this);
        cb.connect();
    }


    public void appendMSG(String message){
        chatArea.append("Server : " + message);
    }

    public void enableInputField(){
        inputField.setEnabled(true);
        clientInfo.setText("Successful in generating symmetric key \n You can send your message!");
    }

    public void connect(){
        sendButton.setEnabled(true);
        connection.setText("Server is connected ! ><");
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
