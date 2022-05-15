package com.example.java2.server;

public class DatabaseAuthService implements AuthService {
    @Override
    public String getNickname(String login, String password) {
        return Database.getUserNickname(login, password);
    }

    @Override
    public boolean changeNickname(String currentNickname, String newNickname) {
        return Database.changeUserNickname(currentNickname, newNickname);
    }
}
