package ru.aberezhnoy.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClientApplication extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("window.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Облачное хранилище");
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
