package ru.geekbains.sklyarov.network.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final int port;
    private final InetAddress ip;
    private List<ClientHandler> clients;
    // for authentication
    private Map<String,String> authMap;

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

    private void start() {
        fillAuthMap();
        this.clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port, 0, ip)) {
            System.out.printf("Server available on %s:%d", ip.toString(), port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // do smth
            System.out.println("finally");
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



    /**
     * Временный метода для реализации авторизации
     * заполнение парой логин - пароль
     */
    private void fillAuthMap(){
        authMap = new ConcurrentHashMap<>();
        authMap.put("Ivanov","123");
        authMap.put("Petrov","456");
        authMap.put("Sidorov","789");
        authMap.put("Sklyarov","0000");
    }

    public boolean checkAuth(String login, String password){
        return authMap.get(login).equals(password);
    }
}
