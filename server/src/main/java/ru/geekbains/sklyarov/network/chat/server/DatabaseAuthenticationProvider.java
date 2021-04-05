package ru.geekbains.sklyarov.network.chat.server;

import java.sql.*;

public class DatabaseAuthenticationProvider implements AuthenticationProvider {
    private DatabaseConnection databaseConnection;

    @Override
    public void init() {
        databaseConnection = new DatabaseConnection();
    }

    @Override
    public String getUserNameByLoginPassword(String login, String password) throws SQLException {
        PreparedStatement prepStmt = databaseConnection.getPreparedStatement("select username from logins where login = ? and password = ?;");
        prepStmt.setString(1, login);
        prepStmt.setString(2, password);
        try (ResultSet result = prepStmt.executeQuery()) {
            if (result.next()) {
                return result.getString("username");
            }
        }
        return null;
    }

    @Override
    public void changeUserName(String oldUserName, String newUserName) throws SQLException {
        PreparedStatement prepStmt = databaseConnection.getPreparedStatement("update logins set username = ? where username = ?;");
        prepStmt.setString(1, newUserName);
        prepStmt.setString(2, oldUserName);
        int result = prepStmt.executeUpdate();
        System.out.println("Changed records: " + result);
    }

    @Override
    public void shutdown() {
        databaseConnection.close();
    }
}
