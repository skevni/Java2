package ru.geekbains.sklyarov.network.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerApp {
    public static void main(String[] args) {

//        HashMap clients = new HashMap<Double, HashMap<String,String>>();
        int clientsCount = 0;

        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("Server listening on port 9000...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected...");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            String someMsg;

            while (true) {
                someMsg = in.readUTF();
                if (someMsg.equals("/stat")) {
                    // переопределять someMsg не стал, чтобы не плодить в памяти объектов,
                    // хотя не уверен на 100%, что так лучше
                    out.writeUTF(String.format("Количество сообщений - %d шт", clientsCount));
                } else {
                    clientsCount++;
                    out.writeUTF(someMsg);
                }
                System.out.println(someMsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
