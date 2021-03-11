package ru.geekbrains.sklyarov.network.chat.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextField msgField;

    @FXML
    TextArea msgArea;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 9000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String someMsg = in.readUTF();
                        msgArea.appendText(someMsg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            thread.start();



        } catch (IOException e) {
//            e.printStackTrace();
            throw new RuntimeException("Unable to connect to server [ localhost:9000 ]");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Unable to send message", ButtonType.OK
            );
            alert.showAndWait();
        }
    }

}
