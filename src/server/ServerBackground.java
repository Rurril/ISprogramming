package server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import util.ChatUtil;
import util.FIleUtil;

public class ServerBackground {
    private final static int SERVER_PORT = 4444;

    private Socket socket;
    private ServerSocket serverSocket;

    // Data and File
    private DataOutputStream out;
    private DataInputStream in;
    private ServerGUI GUI; // GUI object

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

            String key = in.readUTF();
            setSymmetricKey(key);

            String cipherText;

            while(in != null){
                cipherText = in.readUTF();

                // No chatKey key -> means that you can not decrypt the message
                if(isEmptyChatKey()){
                    System.out.println("Server : There is no chatkey");
                    continue;
                }
                // Decrypt cipherText to plainText & Append to chat area
                appendMSG(cipherText);
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void sendFile(String path, String fileName){
        try {
            File file = new File(path);
            InputStream fin = new FileInputStream(file);

            // For sign and verify
            if(serverPrivateKey.isEmpty())return;

            String signature = FIleUtil.sign("JINKWANJEON", serverPrivateKey);
            System.out.println("Signature : " + signature);

            out.writeInt(2); // Type : FILE
            out.writeUTF(signature);
            out.writeUTF(fileName);

            byte[] byteArray = new byte[1024*1024];
            int count = 0;
            while((count = fin.read(byteArray)) > 0){
                out.write(byteArray, 0, count);
            }
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        fileOut;
    }

    public void sendMessage(String message){
        try{
            String encryptedMessage = ChatUtil.encryptMessage(message, chatKey);
            System.out.println(encryptedMessage);
            out.writeInt(1); // Type : MESSAGE
            out.writeUTF(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendMSG(String cipherText){
        try{
            String plainText = ChatUtil.decryptMessage(cipherText, chatKey);
            GUI.appendMSG(plainText);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isEmptyChatKey(){
        return chatKey.isEmpty();
    }

    public void sendPublicKey() throws IOException {
        out.writeInt(3); // Type : PublicKey
        out.writeUTF(serverPublicKey);
        System.out.println("Server : Send PublicKey");
    }
}
