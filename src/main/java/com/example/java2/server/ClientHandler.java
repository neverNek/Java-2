package com.example.java2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private String nick;
    private final DataInputStream in;
    private final DataOutputStream out;
    private AuthService DbAuthService;

    public ClientHandler(Socket socket, ChatServer server, AuthService dbAuthService) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.DbAuthService = DbAuthService;

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания подключения к клиенту", e);
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String msg = in.readUTF(); // /auth login1 pass1
                if (msg.startsWith("/auth")) {
                    final String[] s = msg.split(" "); // s[0] = "/auth", s[1] = "login1", s[2] = "pass1"
                    final String login = s[1];
                    final String password = s[2];
                    final String nick = DbAuthService.getNickByLoginAndPassword(login, password);
                    for (String s1 : s) {
                        System.out.println(s1);
                    }
                    if (nick != null) {
                        if (server.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован");
                            continue;
                        }
                        sendMessage("/authok " + nick); // /authok nick1
                        this.nick = nick;
                        server.broadcast("Пользователь " + nick + " вошёл в чат");
                        server.subscribe(this);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage() {
        try {
            while (true) {
                final String msg = in.readUTF();
                if ("/end".equals(msg)) {
                    break;
                }
                System.out.println("Получено сообщение: " + msg);
                server.broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        sendMessage("/end");

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
        try {
            if (socket != null) {
                server.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка отключения", e);
        }
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Отправляю сообщение: " + message);
            if (nick != null && !message.startsWith("/")) {
                message = this.nick + ": " + message;
            }
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
