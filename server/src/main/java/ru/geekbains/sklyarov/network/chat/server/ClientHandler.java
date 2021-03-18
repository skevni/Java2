package ru.geekbains.sklyarov.network.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
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
                        String[] userNameForVerification = someMsg.split("\\s",3);

                        if (server.isUsernameBusy(userNameForVerification[1])) {
                            sendMessage("/login_failed Current username is already used");
                            continue;
                        }

                        if (!server.checkAuth(userNameForVerification[1],userNameForVerification[2])){
                            sendMessage("/login_failed Wrong(incorrect) login or password");
                            continue;
                        }



                        username = userNameForVerification[1];
                        sendMessage("/login_successful " + username);
                        server.subscribe(this);
                        break;
                    }
                }
                // The cycle of communication with the client
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/")) {
                        executeCommand(someMsg);
                        continue;
                    }

                    // if it's not a private message, send a broadcast message
                    server.broadcastMessage(username + ": " + someMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!socket.isClosed()) {
                    disconnect();
                }
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public synchronized void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    private synchronized void executeCommand(String command) {
// Sending the current client username
        if (command.equals("/who_am_i")) {
            sendMessage("Your username: " + username);
            return;
        }
        // Closing a client socket
        if (command.equals("/exit")) {
            disconnect();
            return;
        }
        // Sending a private message
        if (command.startsWith("/w ")) {
            String[] privateMessage = command.split("\\s", 3);
            server.privateChat(this, privateMessage[1], privateMessage[2]);
            return;
        }
        // username change
        if (command.startsWith("/username_change ")) {
            // если имя введено с пробелом, то возьмем все до пробела
            this.username = command.split("\\s")[1];
            // обновим имя у всех
            server.broadcastClientsList();
        }
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
