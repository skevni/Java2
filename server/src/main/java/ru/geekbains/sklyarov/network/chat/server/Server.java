package ru.geekbains.sklyarov.network.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class Server {
    private final int port;
    private final InetAddress ip;
    private List<ClientHandler> clients;
    // Interface for authentication
    private AuthenticationProvider authenticationProvider;

    // Конструктор класса, если несколько сетевых интерфейсов и необходимо запустить на опеределенном eth
    public Server(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
        start();
    }

    public Server(int port) throws UnknownHostException {
        this.ip = InetAddress.getLocalHost();
        this.port = port;
        start();
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    private void start() {
//        authenticationProvider = new AuthenticationProviderInMemory();
        this.authenticationProvider = new DatabaseAuthenticationProvider();
        this.authenticationProvider.init();
        this.clients = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port, 0, ip)) {
            System.out.printf("Server available on %s:%d", ip.toString(), port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connecting...");
                new ClientHandler(this, socket);
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.authenticationProvider.shutdown();
        }
    }
    /* Пока не знаю как этот метод задействовать для остановки сервера - т.к. он по сути является сервисом(службой)
    *   интерфейса для сервера пока что нет
    */
    public void stop(){
        this.authenticationProvider.shutdown();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler :
                clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void privateChat(ClientHandler sender, String recipientUsername, String message) {
        String returnUsername = sender.getUsername();
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(recipientUsername)) {
                clientHandler.sendMessage(returnUsername + ": " + message);
                // send a message to yourself
                sender.sendMessage(returnUsername + ": " + message);
                return;
            }
        }
        sender.sendMessage("Unable to send message to username: " + recipientUsername + ". User is not found.");
    }

    public void broadcastClientsList() {
        StringBuilder stringBuilder = new StringBuilder("/clients_list;");
        for (ClientHandler cl : clients) {
            stringBuilder.append(cl.getUsername()).append(";");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(stringBuilder.toString());
        }
    }

    public String checkAuthAndGetNickname(String login, String password) {
        try {
            return authenticationProvider.getUserNameByLoginPassword(login, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
