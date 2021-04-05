package ru.geekbains.sklyarov.network.chat.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FillLogins {

    /*
     * The technological class used to fill the table "logins"
     */

    private static class User {
        private final String login;
        private final String password;
        private final String userName;

        public User(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.userName = userName;
        }
    }

    private final Connection connection;

    /*
     * Two technological methods of filling the table "logins"
     * dropAndCreateLogins()
     * fillLogins()
     */
    public FillLogins(Connection connection){
        this.connection = connection;
    }
    private void dropAndCreateLogins() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("create table if not exists " +
                    "logins(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login TEXT NOT NULL, password TEXT NOT NULL, username TEXT NOT NULL);");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void fillLogins() {
        List<User> users = new ArrayList<>(Arrays.asList(
                new User("Bob", "456", "BobB"),
                new User("John", "123", "JohnJ"),
                new User("Ken", "789", "KenK"),
                new User("Sklyarov", "0000", "Evgeniy")
        ));

        try {
            connection.setAutoCommit(false);
            PreparedStatement prepStmt = connection.prepareStatement("DELETE FROM logins;VACUUM logins;");
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
