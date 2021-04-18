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

                int type = in.readInt();
                System.out.println("Type : " + type);
                if(type == 1){ // CASE : MESSAGE

                    String cipherText = in.readUTF();
                    String message = ChatUtil.decryptMessage(cipherText, chatKey);
                    GUI.appendMSG(message);

                }else if(type == 2){ // CASE : FILE

                    if(serverPublicKey.isEmpty())return;

                    String signature = in.readUTF();
                    boolean isVerified = FIleUtil.verifySignarue("JINKWANJEON", signature, serverPublicKey);
                    System.out.println("Verify : " + isVerified);

                    if(isVerified){
                        String fileName = in.readUTF();
                        FileOutputStream fout = new FileOutputStream("./clientFile/" + fileName);
                        byte[] byteArray = new byte[1024*1024];
                        int count = 0;
                        System.out.println(fileName);

                        while((count = in.read(byteArray)) > 0){
                            fout.write(byteArray, 0, count);
                        }
                    }

                }else if(type == 3){ // CASE : Server PublicKey
                    serverPublicKey = in.readUTF();
                    System.out.println("Server's public key : " + serverPublicKey);
                }

            }

//            Thread fileThread = new Thread(new ClientFileThread(socket, this));
//            fileThread.start();

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