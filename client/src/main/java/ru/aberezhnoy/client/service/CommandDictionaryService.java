package ru.aberezhnoy.client.service;

import ru.aberezhnoy.common.domain.Command;

import java.lang.reflect.InvocationTargetException;

public interface CommandDictionaryService {

    void processCommand (Command command);
}
