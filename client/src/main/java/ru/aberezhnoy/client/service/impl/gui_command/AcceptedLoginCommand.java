package ru.aberezhnoy.client.service.impl.gui_command;

import ru.aberezhnoy.client.MainClientApplication;
import ru.aberezhnoy.client.controller.Controller;
import ru.aberezhnoy.client.service.CommandService;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;

public class AcceptedLoginCommand implements CommandService {

    @Override
    public void processCommand (Command command) {
        Controller currentController = (Controller) MainClientApplication.getActiveController();

        currentController.setLogin((String) command.getArgs()[0]);
        currentController.changeLoginPanelToWorkPanel();

        String[] args = {currentController.getLogin()};
        currentController.sendCommand(new Command(CommandType.FILES_LIST.toString(), args));
    }

    @Override
    public String getCommand() {
        return CommandType.LOGIN_OK.toString();
    }
}
