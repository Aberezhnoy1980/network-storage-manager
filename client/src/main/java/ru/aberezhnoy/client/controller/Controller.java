package ru.aberezhnoy.client.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.client.core.NetworkService;
import ru.aberezhnoy.client.factory.Factory;
import ru.aberezhnoy.client.service.impl.ClientPropertiesReceiver;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;
import ru.aberezhnoy.common.domain.FileInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    private static Initializable activeController;

    public static Initializable getActiveController() {
        return activeController;
    }

    private static final Logger LOGGER = LogManager.getLogger(Controller.class);
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
    private TableView<FileInfo> clientFiles;
    @FXML
    private TableView<FileInfo> serverFiles;
    @FXML
    private Button downloadButton;
    @FXML
    private Button uploadButton;

    private NetworkService networkService;
    private String login = null;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        workPanel.setVisible(false);
        downloadButton.setVisible(false);
        uploadButton.setVisible(false);

        initializeNetworkService();

        makeClientTable();
        makeServerTable();

        createClientListFiles(Paths.get(ClientPropertiesReceiver.getClientDirectory()));
    }

    private void initializeNetworkService() {
        networkService = Factory.initializeNetworkService(() -> {
            downloadButton.setDisable(false);
            uploadButton.setDisable(false);
            updateClientListFilesOnGUI(Paths.get(clientPathToFile.getText()));
        });
    }

    private void makeClientTable() {
        clientFiles.getColumns().addAll(createFileTypeColumn(), createFileNameColumn(), createFileSizeColumn());

        moveIntoDirectory(clientFiles, clientPathToFile);
    }

    private TableColumn<FileInfo, String> createFileTypeColumn() {
        TableColumn<FileInfo, String> clientFileTypeColumn = new TableColumn<>("Тип");
        clientFileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileType().getName()));
        clientFileTypeColumn.setPrefWidth(40);
        return clientFileTypeColumn;
    }

    private TableColumn<FileInfo, String> createFileNameColumn() {
        TableColumn<FileInfo, String> clientFileNameColumn = new TableColumn<>("Имя файла");
        clientFileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        clientFileNameColumn.setPrefWidth(240);
        return clientFileNameColumn;
    }

    private TableColumn<FileInfo, Long> createFileSizeColumn() {
        TableColumn<FileInfo, Long> clientFileSizeColumn = new TableColumn<>("Размер файла");
        clientFileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        clientFileSizeColumn.setPrefWidth(120);

        clientFileSizeColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FileInfo, Long> call(TableColumn<FileInfo, Long> column) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            String text = String.format("%,d bytes", item);
                            if (item == -1L) {
                                text = "DIR";
                            }
                            setText(text);
                        }
                    }
                };
            }
        });
        return clientFileSizeColumn;
    }

    private void makeServerTable() {
        serverFiles.getColumns().addAll(createFileTypeColumn(), createFileNameColumn(), createFileSizeColumn());
    }

    private void moveIntoDirectory(TableView<FileInfo> tableView, TextField textField) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path currentPath = Paths.get(textField.getText());
                Path newPath = currentPath.resolve(tableView.getSelectionModel().getSelectedItem().getFileName());
                if (Files.isDirectory(newPath)) {
                    createClientListFiles(newPath);
                }
            }
        });
    }

    public void createClientListFiles(Path path) {
        try {
            clientPathToFile.setText(path.normalize().toAbsolutePath().toString());
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            clientFiles.sort();
        } catch (IOException e) {
            createAlert("Не удалось обновить список файлов");
            LOGGER.throwing(Level.ERROR, e);
        }
    }

    public void createServerListFiles(String path, List<FileInfo> list) {
        serverPathToFile.clear();
        serverPathToFile.setText(path);
        serverFiles.getItems().clear();
        serverFiles.getItems().addAll(list);
        serverFiles.sort();
    }

    public void clientMoveUpInFilePath(ActionEvent actionEvent) {
        Path currentPath = Paths.get(clientPathToFile.getText());
        Path upperPath = currentPath.getParent();
        if (upperPath != null) {
            createClientListFiles(upperPath);
        }
    }

    public void createAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING, text, ButtonType.OK);
        alert.showAndWait();
        LOGGER.info("Клиенту показан Alert " + text);
    }

    public void shutdown() {
        networkService.closeConnection();
    }

    public void login(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }

        if (checkCorrectValueInLoginAndPasswordFields()) {
            String[] textCommand = {CommandType.LOGIN.toString(), loginField.getText(), passwordField.getText()};
            String[] commandArgs = Arrays.copyOfRange(textCommand, 1, textCommand.length);
            networkService.sendCommand(new Command(textCommand[0], commandArgs));

            loginField.clear();
            passwordField.clear();
        }
    }

    private boolean checkCorrectValueInLoginAndPasswordFields() {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            createAlert("Не заполнен логин или пароль. Заполните все поля для авторизации");
            return false;
        }
        return true;
    }

    public void download(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }

        if (!serverFiles.isFocused()) {
            createAlert("Не выбран файл для загрузки в облаке");
        } else {
            uploadButton.setDisable(true);
            downloadButton.setDisable(true);

            networkService.sendCommand(createDownloadCommand());
        }
    }

    private Command createDownloadCommand() {
        Long fileSize = serverFiles.getSelectionModel().getSelectedItem().getSize();
        String userDirectoryForDownload = clientPathToFile.getText();
        Object[] commandArgs = {getSelectedFileName(serverFiles), login, fileSize, userDirectoryForDownload};

        Command command = new Command(CommandType.DOWNLOAD.toString(), commandArgs);

        LOGGER.info("Отправлена команда DOWNLOAD для файла " + getSelectedFileName(serverFiles) + "от клиента" + login + " ,размер файла " + fileSize + " ,директория для загрузки файла " + userDirectoryForDownload);
        return command;
    }

    public void upload(ActionEvent actionEvent) {
        if (!networkService.isConnected()) {
            initializeNetworkService();
        }

        if (!clientFiles.isFocused()) {
            createAlert("Не выбран файл для выгрузки в облако");
        } else {
            uploadButton.setDisable(true);
            downloadButton.setDisable(true);

            networkService.sendCommand(createUploadCommand());
        }
    }

    private Command createUploadCommand() {
        String absolutePathOfUploadFile = getCurrentPath(clientPathToFile) + File.separator + getSelectedFileName(clientFiles);
        Long fileSize = clientFiles.getSelectionModel().getSelectedItem().getSize();
        Object[] commandArgs = {getSelectedFileName(clientFiles), absolutePathOfUploadFile, login, fileSize};

        Command command = new Command(CommandType.UPLOAD.toString(), commandArgs);

        LOGGER.info("Отправлена команда UPLOAD для файла " + getSelectedFileName(clientFiles) + " по пути " + absolutePathOfUploadFile + " от клиента " + login);
        return command;
    }

    public String getSelectedFileName(TableView<FileInfo> tableView) {
        if (!tableView.isFocused()) {
            return null;
        }
        return tableView.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath(TextField textField) {
        return textField.getText();
    }

    public void sendFile(String absolutePathToUploadFile) {
        networkService.sendFile(absolutePathToUploadFile);
    }

    public void sendCommand(Command command) {
        networkService.sendCommand(command);
    }

    public void changeLoginPanelToWorkPanel() {
        Platform.runLater(() -> loginPanel.setVisible(false));

        Platform.runLater(() -> workPanel.setVisible(true));

        Platform.runLater(() -> downloadButton.setVisible(true));

        Platform.runLater(() -> uploadButton.setVisible(true));
    }

    public void createServerListFilesOnGUI(String pathToClientDirectory, List<FileInfo> listOfFiles) {
        Platform.runLater(() -> createServerListFiles(pathToClientDirectory, listOfFiles));
    }

    public void createAlertOnGUI(String textOfAlert) {
        Platform.runLater(() -> createAlert(textOfAlert));
    }

    public void updateClientListFilesOnGUI(Path path) {
        Platform.runLater(() -> createClientListFiles(path));
    }
}
