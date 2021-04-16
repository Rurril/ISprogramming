package client;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientBackground {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private BufferedReader keyInput;
    private InetAddress ia;

    private ClientGUI GUI;


    public void setGUI(ClientGUI GUI){
        this.GUI = GUI;
    }

    public void connect(){
        try{
            ia = InetAddress.getLocalHost(); // To connect to server address
            socket = new Socket(ia, 4444); // Server port is 4444
            System.out.println(socket.toString());

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("HI, I'm client\n"); // initiate message

            while(in != null){
                String message = in.readUTF();
                GUI.appendMsg(message);
            }

        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){

        }
    }

    public void sendMessage(String message){
        try{
            out.writeUTF("Client : " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        ClientBackground clientBackground = new ClientBackground();
//    }
}