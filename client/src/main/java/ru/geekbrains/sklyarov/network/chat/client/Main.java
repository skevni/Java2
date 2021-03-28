package ru.geekbrains.sklyarov.network.chat.client;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Set;


public class Main extends Application {

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client_main.fxml"));

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 500, 300));

        primaryStage.setOnCloseRequest(event -> {
            try {
                stop();

                // Так можно делать? Пока не могу найти правильного решения.
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for (Thread t : threadSet) {
                    t.interrupt();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
