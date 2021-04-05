package ru.geekbains.sklyarov.network.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private String username;
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        /* По идее нужно использовать пул потоков, т.к. много открытых потоков приведет к исчерпанию ресурсов
        * newCachedThreadPool - не стоит использовать, т.к. нет верхней границы, что опять же приведет к исчерпанию
        * системных ресурсов
        * newFixedThreadPool - думаю самый подходящий. Количество потоков при большом количестве клиентов брать равным
        * количеству ядер в системе
        *
        * С увеличением количества потоков происходит увеличение времени преключения контекста процессора, что замедляет
        * работу, т.к. процесор во время переключения контекста не выполняет полезной работы
        *
        */
        pool.execute(()->{
            try {
                // Authorization cycle
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/login ")) {
                        String[] userNameForVerification = someMsg.split("\\s+");
                        if (userNameForVerification.length != 3) {
                            sendMessage("/login_failed Incorrect command for authentication");
                            continue;
                        }
                        String res = server.checkAuthAndGetNickname(userNameForVerification[1], userNameForVerification[2]);
                        if (res == null) {
                            sendMessage("/login_failed Wrong(incorrect) login or password");
                            continue;
                        } else {
                            username = res;
                        }

                        if (server.isUsernameBusy(username)) {
                            sendMessage("/login_failed Current username is already used");
                            continue;
                        }

                        sendMessage("/login_successful " + username);
                        server.subscribe(this);
                        break;
                    }
                }
//                Thread.currentThread().setName("Thread-" + username);
                // The cycle of communication with the client
                while (true) {
                    String someMsg = inputStream.readUTF();
                    if (someMsg.startsWith("/")) {
                        executeCommand(someMsg);
                        continue;
                    }

                    // if it's not a private message, send a broadcast one.
                    server.broadcastMessage(username + ": " + someMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!socket.isClosed()) {
                    disconnect();
                }
            }
        });
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
            String[] privateMessage = command.split("\\s+", 3);
            server.privateChat(this, privateMessage[1], privateMessage[2]);
            return;
        }
        // username change
        if (command.startsWith("/username_change ")) {
            String[] userNameTokens = command.split("\\s+");
            // If the username is entered with a space, return an error message
            if (userNameTokens.length != 2) {
                sendMessage("Server: Incorrect command for change username. Username can't contain a space!");
                return;
            }
            // If the username is busy
            if (server.isUsernameBusy(userNameTokens[1])) {
                sendMessage("Server: Current username is already used.");
                return;
            }
            // Remember username in the InMemory database.

            try {
                server.getAuthenticationProvider().changeUserName(username, userNameTokens[1]);
            } catch (SQLException throwables) {
                sendMessage("Server: Username has not been changed");
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
                e.printStackTrace();
            }
        }
    }

}
