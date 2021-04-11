package ru.geekbains.sklyarov.network.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private String username;

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        /*
        Нет смысла сипользовать пул потоков
         */

        new Thread(() -> {
            try {
                // Authorization cycle
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/login ")) {
                        String[] userNameForVerification = someMsg.split("\\s+");
                        if (userNameForVerification.length != 3) {
                            sendMessage("/login_failed Incorrect command for authentication");
                            logger.debug("Send command to client: \"/login_failed Incorrect command for authentication\" for user {}",userNameForVerification[1]);
                            continue;
                        }
                        String res = server.checkAuthAndGetNickname(userNameForVerification[1], userNameForVerification[2]);
                        if (res == null) {
                            sendMessage("/login_failed Wrong(incorrect) login or password");
                            logger.debug("Send command to client: \"/login_failed Wrong(incorrect) login or password\" for user {}",userNameForVerification[1]);
                            continue;
                        } else {
                            username = res;
                        }

                        if (server.isUsernameBusy(username)) {
                            sendMessage("/login_failed Current username is already used");
                            logger.debug("Send command to client: \"/login_failed Current username is already used\" for user {}",username);
                            continue;
                        }

                        sendMessage("/login_successful " + username);
                        logger.debug("Send command to client: \"/login_successful\" for user {}", username);
                        server.subscribe(this);
                        break;
                    }
                }
                Thread.currentThread().setName("Thread-" + username);
                // The cycle of communication with the client
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/")) {
                        logger.debug("The client sent the command {}", someMsg);
                        executeCommand(someMsg);
                        continue;
                    }

                    // if it's not a private message, send a broadcast one.
                    server.broadcastMessage(username + ": " + someMsg);
                    logger.debug("The client sent the message to everyone");
                }
            } catch (IOException e) {
                logger.error("Error creating user handler", e.fillInStackTrace());
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
            logger.error("Error sending message to user", e.fillInStackTrace());
            disconnect();
        }
    }

    private synchronized void executeCommand(String command) {
// Sending the current client username
        if (command.equals("/who_am_i")) {
            sendMessage("Your username: " + username);
            logger.debug("The client sent a message to know who is he");
            return;
        }
        // Closing a client socket
        if (command.equals("/exit")) {
            logger.debug("The client sent a message to disconnect from the server");
            disconnect();
            return;
        }
        // Sending a private message
        if (command.startsWith("/w ")) {
            String[] privateMessage = command.split("\\s+", 3);
            server.privateChat(this, privateMessage[1], privateMessage[2]);
            logger.debug("The client sent a privat message from {} to {}", username, privateMessage[1]);
            return;
        }
        // username change
        if (command.startsWith("/username_change ")) {
            String[] userNameTokens = command.split("\\s+");
            // If the username is entered with a space, return an error message
            if (userNameTokens.length != 2) {
                sendMessage("Server: Incorrect command for change username. Username can't contain a space!");
                logger.debug("Incorrect command for change username. Username can't contain a space!");
                return;
            }
            // If the username is busy
            if (server.isUsernameBusy(userNameTokens[1])) {
                sendMessage("Server: Current username is already used.");
                logger.debug("Current username is already used. User: {}", userNameTokens[1]);
                return;
            }

            try {
                server.getAuthenticationProvider().changeUserName(username, userNameTokens[1]);
            } catch (SQLException throwables) {
                sendMessage("Server: Username has not been changed");
                logger.debug("Username has not been changed. User from: {}, to: {}", username,userNameTokens[1]);
                return;
            }
            this.username = userNameTokens[1];
            // Update username everywhere
            server.broadcastClientsList();
        }
    }

    private void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error disconnecting user from server", e.fillInStackTrace());
                e.printStackTrace();
            }
        }
    }

}
