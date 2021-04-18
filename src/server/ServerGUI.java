package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
    private JButton loadButton;
    private JButton saveButton;
    private JButton fileButton;
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

        genButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sb.setKeyPair();
                    keyInfo.setText("Key generation is complete! Plz send public key to client");
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
                    sb.sendPublicKey();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDialog.setDirectory(System.getProperty("user.dir"));
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
                saveDialog.setDirectory(System.getProperty("user.dir") + "/serverFile");   // .은 지금폴더
                saveDialog.setVisible(true);
                saveDialog.setFile("serverKey.key"); // TODO : 이걸 어떻게 해야할까...

                // Abnormal termination
                if(saveDialog.getFile() == null) return; // 이걸빼면 취소를 해도 저장이됨
                // 3. 경로명 파일명 설정
                String fileName = saveDialog.getDirectory() + saveDialog.getFile();
                System.out.println(fileName);
                // 4. 파일 저장
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//                    writer.write(txtArea.getText());
//                    writer.close();

                } catch (Exception e2) {

                }
            }
        });

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDialog.setDirectory(System.getProperty("user.dir") + "/server");
                loadDialog.setVisible(true);

                if(loadDialog.getFile() == null)return;

                String fileName = loadDialog.getDirectory() + loadDialog.getFile();
                sb.sendFile(fileName, loadDialog.getFile());
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
        add(genButton); add(loadButton); add(saveButton); add(sendButton); add(fileButton);


        scrollPane.setBounds(100, 0, 400, 200);
        inputField.setBounds(100, 200, 400, 30);
        connection.setBounds(10, 250, 600, 20);

        keyInfo.setBounds(10, 280, 600, 20);
        clientInfo.setBounds(210, 330, 400, 30);
        genButton.setBounds(10, 300, 200, 30);
        loadButton.setBounds(210, 300, 200, 30);
        saveButton.setBounds(410, 300, 200, 30);
        sendButton.setBounds(10, 330, 200, 30);
        fileButton.setBounds(200, 600, 100, 30);

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
        connection.setText("Client is connected ! ><");
    }

    @Override
    public void actionPerformed(ActionEvent e) {    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}
