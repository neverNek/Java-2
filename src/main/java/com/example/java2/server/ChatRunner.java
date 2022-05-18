package com.example.java2.server;

// лаунчер
public class ChatRunner {

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.run();
    }
}
