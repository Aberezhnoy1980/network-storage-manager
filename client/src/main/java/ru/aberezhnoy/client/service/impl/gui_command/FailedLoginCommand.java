package ru.aberezhnoy.client.service.impl.gui_command;

import ru.aberezhnoy.client.MainClientApplication;
import ru.aberezhnoy.client.controller.Controller;
import ru.aberezhnoy.client.service.CommandService;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;

public class FailedLoginCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApplication.getActiveController();
        currentController.createAlertOnGUI("Такой логин уже существует с другим паролем. Введите другую пару логин/пароль для регистрации");
    }

    @Override
    public String getCommand() {
        return CommandType.REGISTRATION_FAILED.toString();
    }
}