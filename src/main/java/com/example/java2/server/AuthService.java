package com.example.java2.server;

import java.io.Closeable;
import java.io.IOException;

public interface AuthService extends Closeable {

    String getNickByLoginAndPassword(String login, String password);

    void start();

    void close() throws IOException;
}
