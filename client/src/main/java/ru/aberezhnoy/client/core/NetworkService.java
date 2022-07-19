package ru.aberezhnoy.client.core;

import ru.aberezhnoy.common.domain.Command;


public interface NetworkService {

    void sendCommand(Command command);

    void closeConnection();

    boolean isConnected();

    void sendFile(String pathToFile);
}
