package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import util.ChatUtil;
import util.FIleUtil;

public class ServerBackground extends JFrame implements ActionListener {
    private final static int SERVER_PORT = 4444;
    private final static String MESSAGE_TO_SERVER = "Hello, Client";

    private Socket socket;
    private ServerSocket serverSocket;
    //    ServerThread serverThread;
    private DataInputStream in;
    private DataOutputStream out;

    private ServerGUI GUI; // GUI object

    // GUI Part
    JTextField field;
    JTextArea chatArea;

    //Security
    private String serverPublicKey;
    private String serverPrivateKey;
    private String clientPublicKey;
    private String chatKey;

    public void setGUI(ServerGUI GUI){
        this.GUI = GUI;
    }

    public void setSymmetricKey(String symmetricKey){
        // It means that you can send your message after encrypt the message
        this.chatKey = symmetricKey;
        GUI.enableInputField();
    }

    public void setKeyPair() throws NoSuchAlgorithmException {
        HashMap<String, String> serverKeyPair = FIleUtil.generateKeyPair();
        serverPublicKey = serverKeyPair.get("publicKey");
        serverPrivateKey = serverKeyPair.get("privateKey");
        System.out.println("Server : Keypair is generated");
    }

    public void setting(){

        try{
            System.out.println("Server Open");
            serverSocket = new ServerSocket(SERVER_PORT);

            socket = serverSocket.accept(); // Create Server, client is waiting
            System.out.println("Accepted from " + socket);
            GUI.connect();

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // At first, must get chatkey(symmetric key) from client.
            String key = in.readUTF();
            setSymmetricKey(key);

            String cipherText;

            while(in != null){
                cipherText = in.readUTF();

                // No chatKey key -> means that you can not decrypt the message
                if(chatKey.isEmpty()){
                    System.out.println("Server : There is no chatkey");
                    continue;
                }
                // Decrypt cipherText to plainText & Append to chat area
                String plainText = ChatUtil.decryptMessage(cipherText, chatKey);
                GUI.appendMSG(plainText);
            }

        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void sendMessage(String message){
        try{
            String encryptedMessage = ChatUtil.encryptMessage(message, chatKey);
            out.writeUTF(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void sendPublicKey() throws IOException {
        out.writeUTF(serverPublicKey);
        System.out.println("Server : Send PublicKey");
    }
}
