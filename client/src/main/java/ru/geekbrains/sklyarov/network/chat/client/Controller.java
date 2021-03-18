package ru.geekbrains.sklyarov.network.chat.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField, usernameField, passwordField;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUsername(null);
        usernameField.setText(System.getenv().get("USERNAME"));
    }

    public void setUsername(String username) {
        if (username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
            splitPanel.setDividerPositions(0.8);
            rightPanel.setVisible(true);
            rightPanel.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
            splitPanel.setDividerPositions(1);
            rightPanel.setVisible(false);
            rightPanel.setManaged(false);
        }
    }

    public void login() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        if (usernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Login cannot be empty", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        try {
            out.writeUTF("/login " + usernameField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("192.168.1.68", 9000);
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to connect to server [ 192.168.1.68:9000 ]", ButtonType.OK);
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Unable to send message", ButtonType.OK
            );
            alert.showAndWait();
        }
    }

    /***
     * Logout button handler
     */
    public void logout() {
        disconnect();
    }
}
