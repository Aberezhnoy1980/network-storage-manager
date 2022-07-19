package ru.aberezhnoy.server.service;

import ru.aberezhnoy.common.domain.Command;

public interface CommandDictionaryService {

    Object processCommand(Command command);
}
