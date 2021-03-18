package ru.geekbains.sklyarov.network.chat.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private final InetAddress ip;
    private List<ClientHandler> clients;

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
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) throws IOException {
        for (ClientHandler clientHandler :
                clients) {
            clientHandler.sendMessage(message);
        }
    }

    public boolean isUsernameBusy(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public ClientHandler privateChat(String username) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getUsername().equals(username)) {
                return clientHandler;
            }
        }
        return null;
    }
}
