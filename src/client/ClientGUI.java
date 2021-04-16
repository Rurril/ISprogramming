package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUI extends JFrame implements ActionListener {

    private JTextField jtf;
    private JTextArea jta;
    private ClientBackground cb = new ClientBackground();

    public ClientGUI(){
        setTitle("Client");
        setBounds(500, 100, 300, 400);

        jta = new JTextArea(40, 25);
        jtf = new JTextField(25);
        add(jta, BorderLayout.CENTER);
        add(jtf, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = jtf.getText() + "\n";
                jta.append("Client : "+ message);
                jtf.setText("");

                cb.sendMessage(message);


            }
        });

        cb.setGUI(this);
        cb.connect();
    }

    public void appendMsg(String message){
        jta.append(message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
