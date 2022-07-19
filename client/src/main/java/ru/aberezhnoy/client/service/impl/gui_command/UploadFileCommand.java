package ru.aberezhnoy.client.service.impl.gui_command;

import ru.aberezhnoy.client.MainClientApplication;
import ru.aberezhnoy.client.controller.Controller;
import ru.aberezhnoy.client.service.CommandService;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;

public class UploadFileCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApplication.getActiveController();
        currentController.sendFile((String) command.getArgs()[1]);
    }

    @Override
    public String getCommand() {
        return CommandType.READY_TO_UPLOAD.toString();
    }
}
