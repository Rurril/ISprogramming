package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ServerGUI extends JFrame implements ActionListener{

    // GUI Part
    private JTextField inputField;
    private JTextArea chatArea;
    private JTextArea connection;
    private JTextArea clientInfo;
    private ServerBackground sb;

    private JButton sendButton;

    ServerGUI() {

        sb = new ServerBackground();
        setTitle("server");


        chatArea = new JTextArea();
        chatArea.setEditable(false);
        inputField = new JTextField();
        inputField.setEnabled(false);

        connection = new JTextArea("Not connected");
        JTextArea keyInfo = new JTextArea("There is no key & Cannot connect to client");
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

        /*
        End of Button Part
         */
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "Server : " + inputField.getText() + "\n";
                chatArea.append(message);
                inputField.setText("");
                sb.sendMessage(message);
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


        scrollPane.setBounds(0, 0, 400, 200);
        inputField.setBounds(0, 200, 400, 30);
        connection.setBounds(10, 250, 600, 20);

        keyInfo.setBounds(10, 280, 600, 20);
        clientInfo.setBounds(210, 330, 400, 30);
        genButton.setBounds(10, 300, 200, 30);
        loadButton.setBounds(210, 300, 200, 30);
        saveButton.setBounds(410, 300, 200, 30);
        sendButton.setBounds(10, 330, 200, 30);


        sb.setGUI(this);
        sb.setting();
    }


    public void appendMSG(String message){
        chatArea.append(message);
        System.out.println("Message from client : " + message);
    }

    public void enableInputField(){
        inputField.setEnabled(true);
        clientInfo.setText("Successful in receiving Client's publicKey \n You can send your message!");
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
