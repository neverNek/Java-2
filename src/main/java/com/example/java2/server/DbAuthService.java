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
        try (PreparedStatement statement =
                     connection.prepareStatement("select nick from client where login = ? and password = ?")) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
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
            createTable(connection);
            for (int i = 0; i < 5; i++) {
                insert(connection, "login" + i, "pass" + i, "nick" + i);
            }
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

    private static void createTable(Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("" +
                " CREATE TABLE IF NOT EXISTS chat-clients (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    login TEXT, " +
                "    password TEXT" +
                "    nick TEXT" +
                ")")) {
            statement.executeUpdate();
        }
    }

    private static void insert(Connection connection, String login, String password, String nick) throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO chat-clients (login, password, nick) VALUES (?, ?, ?)")) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setString(3, nick);
            statement.executeUpdate();
        }
    }

}
