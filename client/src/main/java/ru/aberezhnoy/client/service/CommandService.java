package ru.aberezhnoy.client.service;

import ru.aberezhnoy.common.domain.Command;

import java.lang.reflect.InvocationTargetException;

public interface CommandService {

    void processCommand (Command command);

    String getCommand();
}
