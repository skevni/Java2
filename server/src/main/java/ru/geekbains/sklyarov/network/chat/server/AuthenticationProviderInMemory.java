package ru.geekbains.sklyarov.network.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthenticationProviderInMemory implements AuthenticationProvider {
    private class User {
        private String login;
        private String password;
        private String userName;

        public User(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.userName = userName;
        }

    }

    List<User> users;

    // Constructor
    public AuthenticationProviderInMemory() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("John", "123", "JohnJ"),
                new User("Bob", "456", "BobB"),
                new User("Ken", "789", "KenK")
        ));
    }

    @Override
    public String getUserNameByLoginPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)){
                return user.userName;
            }
        }
        return null;
    }

    @Override
    public void changeUserName(String oldUserName, String newUserName) {
        for (User user : users) {
            if (user.userName.equals(oldUserName)){
                user.userName = newUserName;
                return ;
            }
        }
    }
}
