package ru.geekbains.sklyarov.network.chat.server;

import java.sql.SQLException;

public interface AuthenticationProvider {
    String getUserNameByLoginPassword(String login, String password) throws SQLException;

    void changeUserName(String oldUserName, String newUserName) throws SQLException;
}
