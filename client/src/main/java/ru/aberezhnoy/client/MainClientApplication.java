package ru.aberezhnoy.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.aberezhnoy.client.controller.Controller;

public class MainClientApplication extends Application {

    private static Initializable activeController;

    public static Initializable getActiveController() {
        return activeController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gui/MainWindow.fxml"));
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Облачное хранилище");
        primaryStage.setResizable(true);

        Controller controller = loader.getController();
        activeController = controller;
        primaryStage.setOnCloseRequest(event -> controller.shutdown());
        primaryStage.show();
    }
}
