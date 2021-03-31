package ru.geekbrains.sklyarov.network.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.InetAddress;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField, usernameField;
    @FXML
    PasswordField passwordField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, messagePanel;

    @FXML
    VBox rightPanel;

    @FXML
    ListView<String> clients_view;

    @FXML
    SplitPane splitPanel;

    @FXML
    Button btnLogout;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private Logger logger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUsername(null);
        usernameField.setText(System.getenv().get("USERNAME"));
    }

    public void setUsername(String username) {
        boolean isUserNameNull = username == null;

        loginPanel.setVisible(isUserNameNull);
        loginPanel.setManaged(isUserNameNull);
        messagePanel.setVisible(!isUserNameNull);
        messagePanel.setManaged(!isUserNameNull);
        splitPanel.setDividerPositions(1);
        rightPanel.setVisible(!isUserNameNull);
        rightPanel.setManaged(!isUserNameNull);
    }

    public void login() {
        if (usernameField.getText().isEmpty()) {
            alertError("Login cannot be empty");
            return;
        }

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/login " + usernameField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket(InetAddress.getLocalHost().getCanonicalHostName(), 9000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String someMsg = in.readUTF();
                        // Successful authorization
                        if (someMsg.startsWith("/login_successful ")) {
                            setUsername(someMsg.split("\\s")[1]);
                            break;
                        }
                        if (someMsg.startsWith("/login_failed ")) {
                            msgArea.appendText(someMsg.split("\\s", 2)[1] + "\n");
                        }
                    }
                    // вывод истории
                    logger = new Logger(String.format("logs/history_%s.txt", usernameField.getText()));
                    msgArea.appendText(logger.readFromFile());

                    while (true) {
                        String someMsg = in.readUTF();

                        if (someMsg.startsWith("/")) {

                            if (someMsg.startsWith("/clients_list;")) {
                                String[] clientsParts = someMsg.split(";");
                                // clear ListView
                                Platform.runLater(() -> {
                                    clients_view.getItems().clear();
                                    for (int i = 1; i < clientsParts.length; i++) {
                                        clients_view.getItems().add(clientsParts[i]);
                                    }
                                });
                            }
                            continue;
                        }

                        msgArea.appendText(someMsg + "\n");
                        logger.writeToFile(someMsg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }

            });
            thread.start();
        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Unable to connect to server [ 192.168.1.68:9000 ]");
            alertError("Unable to connect to server [ 192.168.1.68:9000 ]");
            throw new RuntimeException("Unable to connect to server [ 192.168.1.68:9000 ]");
        }
    }

    public void disconnect() {
        setUsername(null);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            alertError("Unable to send message");
        }
    }

    /***
     * Logout button handler
     */
    public void logout() {
        disconnect();
        passwordField.clear();
    }

    public void alertError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void alertInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
