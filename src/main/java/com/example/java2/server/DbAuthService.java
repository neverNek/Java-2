package com.example.java2.server;

import java.io.IOException;
import java.sql.*;

public class DbAuthService implements AuthService {

    private static final String DB_CONNECTION_URL = "jdbc:sqlite:chat-clients.db";

    private Connection connection;

    public DbAuthService() {
        start();
    }
    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        try (final PreparedStatement statement = connection.prepareStatement("select nick from client where login = ? and password = ?")) {
            statement.setString(1, login);
            statement.setString(2, password);
            final ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("nick");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void start() {
        try {
            connection = DriverManager.getConnection(DB_CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка закрытия соединения с БД", e);
        }
    }

}
