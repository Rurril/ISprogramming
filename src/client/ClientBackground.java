package client;

import util.ChatUtil;
import util.FIleUtil;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ClientBackground {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private InetAddress ia;

    private ClientGUI GUI; // GUI object

    // GUI Part
    JTextField field;
    JTextArea chatArea;

    //Security
    private String clientPublicKey;
    private String clientPrivateKey;
    private String serverPublicKey;
    private String chatKey;


    public void setGUI(ClientGUI GUI){
        this.GUI = GUI;
    }

    public void setSymmetricKey(String symmetricKey){
        // It means that you can send your message after encrypt the message
        this.chatKey = symmetricKey;
        GUI.enableInputField();
    }

    public void setKeyPair() throws NoSuchAlgorithmException {
        HashMap<String, String> clientKeyPair = FIleUtil.generateKeyPair();
        clientPublicKey = clientKeyPair.get("publicKey");
        clientPrivateKey = clientKeyPair.get("privateKey");
        System.out.println("Client : Keypair is generated");
    }

    public void connect(){
        try{
            ia = InetAddress.getLocalHost(); // To connect to server address
            socket = new Socket(ia, 4444); // Server port is 4444
            System.out.println(socket.toString());
            GUI.connect();

            // Generate key using 'AES128' - symmetric key
            setSymmetricKey(ChatUtil.generateKey());

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Initial transfer - symmetric key
            // For secure chatting
            out.writeUTF(chatKey);

            while(in != null){
                String cipherText = in.readUTF();
                String message = ChatUtil.decryptMessage(cipherText, chatKey);
                GUI.appendMSG(message);
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){

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

    public void sendPublicKey() throws IOException {
        out.writeUTF(clientPublicKey);
        System.out.println("Client : Send PublicKey");
    }

//    public static void main(String[] args) {
//        ClientBackground clientBackground = new ClientBackground();
//    }
}