package ru.geekbains.sklyarov.network.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    // logging
    private static final Logger logger = LogManager.getLogger(Server.class);

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
        /*
         * Здесь создастся коннект (Connection) и потом можно к нему обращаться как DatabaseAuthenticationProvider.connection
         * На уроке говорили ,что нужно отдельный класс для этого сделать. А так нельзя?
         */
        authenticationProvider = new DatabaseAuthenticationProvider();
        this.clients = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port, 0, ip)) {
            logger.info("Server available on {}:{}", ip.toString(), port);
//            System.out.printf("Server available on %s:%d", ip.toString(), port);
            while (true) {
                Socket socket = serverSocket.accept();
                logger.debug("Client connecting...");
//                System.out.println("Client connecting...");
                /*
                    Нет смысла ипользовать пул потоков, это не ускорит работу
                 */
                new ClientHandler(this, socket);
//                System.out.println("Client connected");
                logger.debug("Client connected");
            }
        } catch (IOException e) {
            logger.error("Server socket creation error", e.fillInStackTrace());
//            e.printStackTrace();
        }
    }

    /* Пока не знаю как этот метод задействовать для остановки сервера - т.к. он по сути является сервисом(службой)
     *   интерфейса для сервера пока что нет
     */
    public void stop() {
        try {
            authenticationProvider.databaseDisconnect();
        } catch (SQLException e) {
            logger.error("Server stop error", e.fillInStackTrace());
//            e.printStackTrace();
        }
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
            logger.error("User authentication error",throwables.fillInStackTrace());
        }
        return null;
    }
}
