package ru.geekbains.sklyarov.network.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseAuthenticationProvider implements AuthenticationProvider {

    public static Connection connection;
    /* Если статический Statement или PreparedStatement, то он же будет один для всех экземпляров данного класса?
        Можно ли делать общий для всех (в том числе и при многопоточности)? Не будут ли CRUD операции
        разных объектов мешать друг другу?
        Или все же при connection.prepareStatement создается уникальный экземпляр PreparedStatement для каждого
        экземпляра класса?
    */
    public static PreparedStatement prepStmt;

    private static final Logger log = LogManager.getLogger(DatabaseAuthenticationProvider.class);

    // The constructor
    public DatabaseAuthenticationProvider() {
        databaseConnect();
    }

    public void databaseConnect()  {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:local.db");
//            stmt = connection.createStatement();
//            dropAndCreateLogins();
//            fillLogins();

        } catch (ClassNotFoundException | SQLException e) {
            log.error("Unable to connect to database", e);
            throw new RuntimeException("Unable to connect to database");
        }
    }
    @Override
    public void databaseDisconnect() throws SQLException {
        if (prepStmt != null) {
            prepStmt.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public String getUserNameByLoginPassword(String login, String password) throws SQLException {
        prepStmt = connection.prepareStatement("select username from logins where login = ? and password = ?;");
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
        prepStmt = connection.prepareStatement("update logins set username = ? where username = ?;");
        prepStmt.setString(1, newUserName);
        prepStmt.setString(2, oldUserName);
        int result = prepStmt.executeUpdate();

    }

    /*
     * Two technological methods of filling the table "logins"
     * dropAndCreateLogins()
     * fillLogins()
     */

    private void dropAndCreateLogins() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("create table if not exists " +
                    "logins(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT NOT NULL, password TEXT NOT NULL, username TEXT NOT NULL);");
        } catch (SQLException throwables) {
            log.error("Error in the method of creating and dropping a table", throwables);
//            throwables.printStackTrace();
        }
    }
}
