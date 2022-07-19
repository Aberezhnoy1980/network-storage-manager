package ru.aberezhnoy.client.service.impl.gui_command;

import ru.aberezhnoy.client.MainClientApplication;
import ru.aberezhnoy.client.controller.Controller;
import ru.aberezhnoy.client.service.CommandService;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;
import ru.aberezhnoy.common.domain.FileInfo;

import java.util.List;

public class CloudFilesListCommand implements CommandService {

    @Override
    public void processCommand(Command command) {
        Controller currentController = (Controller) MainClientApplication.getActiveController();

        currentController.createServerListFilesOnGUI((String) command.getArgs()[0], (List<FileInfo>) command.getArgs()[1]);
    }

    @Override
    public String getCommand() {
        return CommandType.CLOUD_FILES_LIST.toString();
    }
}
