package com.example.java2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final ClientController controller;

    ExecutorService service = Executors.newFixedThreadPool(10);

    public ChatClient(ClientController controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        service.execute(() -> {
            try {
                waitAuth();
                readMessage();
            } finally {
                closeConnection();
            }
        });

        service.shutdown();

//        new Thread(() -> {
//            try {
//                waitAuth();
//                readMessage();
//            } finally {
//                closeConnection();
//            }
//        }).start();

    }

    private void waitAuth() {
        while (true) {
            try {
                final String msg = in.readUTF(); // /authok nick
                if (msg.startsWith("/authok")) {
                    final String[] split = msg.split(" ");
                    final String nick = split[1];
                    controller.toggleBoxesVisibility(true);
                    controller.addMessage("Успешная авторизация под ником " + nick);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage() {
        while (true) {
            try {
                String msg = in.readUTF();
                if ("/end".equals(msg)) {
                    controller.toggleBoxesVisibility(false);
                    break;
                }
                controller.addMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
