package ru.geekbains.sklyarov.network.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String username;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                // Authorization cycle
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/login ")) {
                        String userNameForVerification = someMsg.split("\\s")[1];
                        if (server.isUsernameBusy(userNameForVerification)) {
                            sendMessage("/login_failed Current username is already used");
                            continue;
                        }

                        username = userNameForVerification;
                        sendMessage("/login_successful " + username);
                        server.subscribe(this);
                        break;
                    }
                }
                // The cycle of communication with the client
                while (true) {
                    String someMsg = inputStream.readUTF();
                    // Sending the current client username
                    if (someMsg.equals("/who_am_i")) {
                        outputStream.writeUTF("Your username: " + username);
                        continue;
                    }
                    // Closing a client socket
                    if (someMsg.equals("/exit")) {
                        disconnect();
                    }
                    // Sending a private message
                    if (someMsg.startsWith("/w ")) {
                        String[] privateMessage = someMsg.split("\\s", 3);
                        ClientHandler cl = server.privateChat(privateMessage[1]);
                        if (cl != null) {
                            cl.sendMessage(username + ": " + privateMessage[2]);
                            sendMessage(username + ": " + privateMessage[2]);
                        }
                        continue;
                    }
                    // if it's not a private message, send a broadcast message
                    server.broadcastMessage(username + ": " + someMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public synchronized void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    private void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
