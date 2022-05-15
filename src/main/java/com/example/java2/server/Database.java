package com.example.java2.server;


import java.sql.*;

public class Database {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement createUserStatement;
    private static PreparedStatement getUserNicknameStatement;
    private static PreparedStatement changeUserNicknameStatement;
    private static PreparedStatement deleteUserStatement;

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database/chat.db");
            System.out.println("Connected to the database");
            statement = connection.createStatement();
            createUserTable();
            prepareAllStatement();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUserTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS user (" +
                "    id       INTEGER      PRIMARY KEY AUTOINCREMENT" +
                "                          NOT NULL" +
                "                          UNIQUE," +
                "    login    VARCHAR (32) UNIQUE" +
                "                          NOT NULL," +
                "    password VARCHAR (32) NOT NULL," +
                "    nickname VARCHAR (32) UNIQUE" +
                "                          NOT NULL" +
                ");"
        );
    }

    public static void prepareAllStatement() throws SQLException {
        createUserStatement = connection.prepareStatement("INSERT INTO user (login, password, nickname) VALUES (?, ?, ?);");
        getUserNicknameStatement = connection.prepareStatement("SELECT nickname FROM user WHERE login = ? AND password = ?;");
        changeUserNicknameStatement = connection.prepareStatement("UPDATE user SET nickname = ? WHERE nickname = ?;");
        deleteUserStatement = connection.prepareStatement("DELETE FROM user WHERE login = ?;");
    }

    public static boolean createUser(String login, String password, String nickname) {
        try {
            createUserStatement.setString(1, login);
            createUserStatement.setString(2, password);
            createUserStatement.setString(3, nickname);
            createUserStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getUserNickname(String login, String password) {
        String nickname = null;
        try {
            getUserNicknameStatement.setString(1, login);
            getUserNicknameStatement.setString(2, password);
            ResultSet rs = getUserNicknameStatement.executeQuery();
            if (rs.next()) {
                nickname = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public static boolean changeUserNickname(String currentNickname, String newNickname) {
        try {
            changeUserNicknameStatement.setString(1, newNickname);
            changeUserNicknameStatement.setString(2, currentNickname);
            changeUserNicknameStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean deleteUser(String login) {
        try {
            deleteUserStatement.setString(1, login);
            deleteUserStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
