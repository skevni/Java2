package ru.geekbrains.sklyarov.network.chat.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Set;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client_main.fxml"));

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 300, 275));

//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//                for (Thread t :
//                        threadSet) {
//                    t.interrupt();
//                }
//            }
//        });

        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
