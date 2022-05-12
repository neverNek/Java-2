package com.example.java2;

import java.sql.*;

public class ClientDB {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:clientdb.db")) {
//            createTable(connection);
//            insert(connection, "login0", "pas0");
//            insert(connection, "login1", "pas1");
//            insert(connection, "login2", "pas2");
//            insert(connection, "login3", "pas3");
//            insert(connection, "login4", "pas4");
            select(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void createTable(Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("" +
                " CREATE TABLE IF NOT EXISTS clientdb (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    login TEXT, " +
                "    password TEXT" +
                ")")) {
            statement.executeUpdate();
        }
    }

    private static void insert(Connection connection, String login, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO clientdb (login, password) VALUES (?, ?)")) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
        }
    }

    private static void select(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM clientdb")) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");
                String password = rs.getString("password");
                System.out.printf("%d - %s - %s\n", id, login, password);
            }
        }
    }

    private static void selectByLogin(Connection connection, String login) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM clientdb WHERE login = ?")) {
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String loginDB = rs.getString("login");
                String password = rs.getString("password");
                System.out.printf("%d - %s - %s\n", id, loginDB, password);
            }
        }
    }

    private static void dropById(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM clientdb WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

    }
}
