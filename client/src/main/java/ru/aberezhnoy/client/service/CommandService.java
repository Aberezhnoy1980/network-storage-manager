package ru.aberezhnoy.client.service;

import ru.aberezhnoy.common.domain.Command;

public interface CommandService {

    void processCommand (Command command);

    String getCommand();
}
