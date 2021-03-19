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
    private Map<String, Map<String, String>> authMap;

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
                System.out.println("Client connecting...");
                new ClientHandler(this, socket);
                System.out.println("Client connected");
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
    private void fillAuthMap() {
        authMap = new ConcurrentHashMap<>();

        Map<String, String> data = new ConcurrentHashMap<>();
        data.put("123", "IvanovI");
        authMap.put("Ivanov", data);
        Map<String, String> data2 = new ConcurrentHashMap<>();
        data2.put("456", "PetrovP");
        authMap.put("Petrov", data2);
        Map<String, String> data3 = new ConcurrentHashMap<>();
        data3.put("789", "SidorovS");
        authMap.put("Sidorov", data3);
        Map<String, String> data4 = new ConcurrentHashMap<>();
        data4.put("0000", "SklyarovEvgeniy");
        authMap.put("Sklyarov", data4);
//        authMap.put("Ivanov", new String[][]);
//        authMap.put("Petrov", new String[]{"456", "PetrovP"});
//        authMap.put("Sidorov",(Map<String, String>) (new ConcurrentHashMap<>()).put("789","SidorovSidor"));
//        authMap.put("Sklyarov",(Map<String, String>) (new ConcurrentHashMap<>()).put("0000","SklyarovEvgeniy"));
    }

    public String checkAuthAndGetNickname(String login, String password) {
        if (authMap.get(login) == null) {
            return null;
        }
        return authMap.get(login).getOrDefault(password, null);
    }
}
