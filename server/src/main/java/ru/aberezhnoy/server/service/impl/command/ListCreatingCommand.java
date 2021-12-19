package ru.aberezhnoy.server.service.impl.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.common.domain.CommandType;
import ru.aberezhnoy.common.domain.FileInfo;
import ru.aberezhnoy.server.factory.Factory;
import ru.aberezhnoy.server.service.CommandService;
import ru.aberezhnoy.server.service.impl.ListOfClientFilesInCloudCreatingService;

import java.util.List;

public class ListCreatingCommand implements CommandService {

    private static final Logger LOGGER = LogManager.getLogger(ListCreatingCommand.class);

    @Override
    public List<FileInfo> processCommand(Command command) {
        final int requirementCountCommandArgs = 1;
        if (command.getArgs().length != requirementCountCommandArgs) {
            LOGGER.error("Command " + getCommand() + " is not correct");
            throw new IllegalArgumentException("Command " + getCommand() + " is not correct");
        }
        return process((String) command.getArgs()[0]);
    }

    private List<FileInfo> process(String login) {
        ListOfClientFilesInCloudCreatingService listOfClientFilesInCloudCreatingService = Factory.getListOfFilesService();
        return listOfClientFilesInCloudCreatingService.createServerFilesList(login);
    }

    @Override
    public String getCommand() {
        return CommandType.FILES_LIST.toString();
    }
}
