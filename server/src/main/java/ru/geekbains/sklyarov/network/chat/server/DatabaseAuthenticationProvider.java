package ru.geekbains.sklyarov.network.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseAuthenticationProvider implements AuthenticationProvider {
    /*
     * The technological class used to fill the table "logins"
     */

    private class User {
        private final String login;
        private final String password;
        private final String userName;

        public User(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.userName = userName;
        }

    }

    public static Connection connection;
    /* Если статический Statement или PreparedStatement, то он же будет один для всех экземпляров данного класса?
        Можно ли делать общий для всех (в том числе и при многопоточности)? Не будут ли CRUD операции
        разных объектов мешать друг другу?
        Или все же при connection.prepareStatement создается уникальный экземпляр PreparedStatement для каждого
        экземпляра класса?
    */
    public static PreparedStatement prepStmt;

    // The constructor
    public DatabaseAuthenticationProvider() {
        databaseConnect();
    }

    public void databaseConnect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:local.db");
//            stmt = connection.createStatement();
//            dropAndCreateLogins();
//            fillLogins();

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Unable to connect to database");
        }
    }

    public void databaseDisconnect() {
        try {
            if (prepStmt != null) {
                prepStmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getUserNameByLoginPassword(String login, String password) throws SQLException {
        prepStmt = connection.prepareStatement("select username from logins where login = ? and password = ?;");
        prepStmt.setString(1, login);
        prepStmt.setString(2, password);
        connection.setAutoCommit(false);
        try (ResultSet result = prepStmt.executeQuery()) {
            if (result.next()) {
                return result.getString("username");
            }
        } finally {
            connection.setAutoCommit(true);
        }
        return null;
    }

    @Override
    public void changeUserName(String oldUserName, String newUserName) throws SQLException {
        prepStmt = connection.prepareStatement("update logins set username = ? where username = ?;");
        prepStmt.setString(1, newUserName);
        prepStmt.setString(2, oldUserName);
        connection.setAutoCommit(false);
        int result = prepStmt.executeUpdate();

        connection.setAutoCommit(true);
        System.out.println("Changed records: " + result);
    }

    /*
     * Two technological methods of filling the table "logins"
     * dropAndCreateLogins()
     * fillLogins()
     */

    private void dropAndCreateLogins() {
        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            stmt.executeUpdate("create table if not exists " +
                    "logins(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT NOT NULL, password TEXT NOT NULL, username TEXT NOT NULL);");
            connection.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void fillLogins() {
        List<User> users = new ArrayList<>(Arrays.asList(
                new User("John", "123", "JohnJ"),
                new User("Bob", "456", "BobB"),
                new User("Ken", "789", "KenK"),
                new User("Sklyarov", "0000", "Evgeniy")
        ));

        try {
            connection.setAutoCommit(false);
            prepStmt = connection.prepareStatement("DELETE FROM logins;VACUUM logins;");
            prepStmt.executeUpdate();

            connection.commit();

            prepStmt = connection.prepareStatement("Insert into logins (login, password, username) values(?,?,?);");
            for (User u : users) {
                prepStmt.setString(1, u.login);
                prepStmt.setString(2, u.password);
                prepStmt.setString(3, u.userName);
                prepStmt.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


}
