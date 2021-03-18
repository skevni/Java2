package ru.geekbrains.sklyarov.network.chat.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    @FXML
    TextField msgField, usernameField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, messagePanel;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public void setUsername(String username) {
        this.username = username;
        if (username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
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
            out.writeUTF("/login " + usernameField.getText());
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
                            // Успешная авторизация
                            if (someMsg.startsWith("/login_successful ")) {
                                setUsername(someMsg.split("\\s")[1]);
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


}
