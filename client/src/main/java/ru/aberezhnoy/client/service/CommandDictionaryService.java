package ru.aberezhnoy.client.service;

import ru.aberezhnoy.common.domain.Command;

public interface CommandDictionaryService {

    void processCommand (Command command);
}
