package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class ClientGUI extends JFrame implements ActionListener {

    // GUI Part
    private JTextField inputField;
    private JTextArea chatArea;
    private JTextArea connection;
    private JTextArea serverInfo;
    private ClientBackground cb;

    private JButton sendButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton fileButton;

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

        serverInfo = new JTextArea("There is no server's PublicKey");

        /*
        dialog part
         */
        FileDialog loadDialog = new FileDialog(this, "Load", FileDialog.LOAD);
        FileDialog saveDialog = new FileDialog(this, "Save", FileDialog.SAVE);


        /*
        Buttons, and actionListeners
         */

        JButton genButton = new JButton("Key generation");
        loadButton = new JButton("Load from a file");
        saveButton = new JButton("Save into a file");
        sendButton = new JButton("Send public key");
        sendButton.setEnabled(false);
        fileButton = new JButton("Send File");
        fileButton.setEnabled(false);

        genButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cb.setKeyPair();
                    keyInfo.setText("Key generation is complete! Plz send public key to server");
                    genButton.setEnabled(false);
                    sendButton.setEnabled(true);
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
                    fileButton.setEnabled(true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDialog.setDirectory(System.getProperty("user.dir") + "clientFile");
                loadDialog.setVisible(true);

                if(loadDialog.getFile() == null)return;

                String fileName = loadDialog.getDirectory() + loadDialog.getFile();
                try{
                    BufferedReader br = new BufferedReader(new FileReader(fileName));

                    String line;
                    while((line = br.readLine()) != null){
                        System.out.println(line);
                    }
                }catch (Exception e2){

                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // File directory setting
                saveDialog.setDirectory(System.getProperty("user.dir") + "/clientFile");
                saveDialog.setVisible(true);
                saveDialog.setFile("clientKey.key");

                // Abnormal termination
                if(saveDialog.getFile() == null) return;

                String fileName = saveDialog.getDirectory() + saveDialog.getFile();
                System.out.println(fileName);

                // Save file
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//                    writer.write(txtArea.getText());
                    writer.close();

                } catch (Exception e2) {

                }
            }
        });

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDialog.setDirectory(System.getProperty("user.dir") + "/clientFile");
                loadDialog.setVisible(true);

                if(loadDialog.getFile() == null)return;

                String fileName = loadDialog.getDirectory() + loadDialog.getFile();
                cb.sendFile(fileName, loadDialog.getFile());
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
        setSize(600, 700);
        setLayout(null);

        add(scrollPane);
        add(inputField);
        add(connection);
        add(keyInfo); add(serverInfo);
        add(genButton); add(loadButton); add(saveButton); add(sendButton); add(fileButton);


        scrollPane.setBounds(100, 0, 400, 200);
        inputField.setBounds(100, 200, 400, 30);
        connection.setBounds(10, 250, 600, 20);

        keyInfo.setBounds(10, 280, 600, 20);
        serverInfo.setBounds(210, 330, 400, 60);
        genButton.setBounds(10, 300, 200, 30);
        loadButton.setBounds(210, 300, 200, 30);
        saveButton.setBounds(410, 300, 200, 30);
        sendButton.setBounds(10, 330, 200, 30);
        fileButton.setBounds(150, 400, 300, 30);

        cb.setGUI(this);
        cb.connect();
    }


    public void appendMSG(String message){
        chatArea.append("Server : " + message);
    }

    public void enableInputField(){
        inputField.setEnabled(true);
        serverInfo.setText("Successful in generating symmetric key \n You can send your message!");
    }

    public void connect(){
        connection.setText("Server is connected ! ><");
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
