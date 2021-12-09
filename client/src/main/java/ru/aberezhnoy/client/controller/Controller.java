package ru.aberezhnoy.client.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public HBox loginPanel;
    public HBox workPanel;

    public TextField loginField;
    public TextField clientPathToFile;
    public TextField serverPathToFile;

    public PasswordField passwordField;

    public TableView clientFiles;
    public TableView serverFiles;

    public Button downloadButton;
    public Button uploadButton;

    private String login = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workPanel.setVisible(false);
        downloadButton.setVisible(false);
        uploadButton.setVisible(false);
    }
}
