package ru.geekbains.sklyarov.network.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class ServerApp {
    public static void main(String[] args) {
        try {
            new Server(9000);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host");
        }
    }
}
