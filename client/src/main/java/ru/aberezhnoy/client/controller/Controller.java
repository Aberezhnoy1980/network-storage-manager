package ru.aberezhnoy.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import ru.aberezhnoy.client.core.NetworkService;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private HBox loginPanel;
    @FXML
    private HBox workPanel;
    @FXML
    private TextField loginField;
    @FXML
    private TextField clientPathToFile;
    @FXML
    private TextField serverPathToFile;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TableView clientFiles;
    @FXML
    private TableView serverFiles;
    @FXML
    private Button downloadButton;
    @FXML
    private Button uploadButton;

    private NetworkService networkService;
    private String login = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workPanel.setVisible(true);
        downloadButton.setVisible(true);
        uploadButton.setVisible(true);
    }
}
