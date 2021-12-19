package ru.aberezhnoy.server.factory;

import ru.aberezhnoy.server.core.NettyServerService;
import ru.aberezhnoy.server.core.ServerService;
import ru.aberezhnoy.server.service.AuthenticationService;
import ru.aberezhnoy.server.service.CommandDictionaryService;
import ru.aberezhnoy.server.service.CommandService;
import ru.aberezhnoy.server.service.DatabaseConnectionService;
import ru.aberezhnoy.server.service.impl.CommandDictionaryServiceImpl;
import ru.aberezhnoy.server.service.impl.ListOfClientFilesInCloudCreatingService;
import ru.aberezhnoy.server.service.impl.command.AuthenticationCommand;
import ru.aberezhnoy.server.service.impl.command.ListCreatingCommand;
import ru.aberezhnoy.server.service.impl.databaseConnection.AuthenticationServiceImpl;
import ru.aberezhnoy.server.service.impl.databaseConnection.DatabaseConnectionServiceImpl;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService();
    }

    public static CommandDictionaryService getCommandDictionaryService() {
        return new CommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices() {
        return Arrays.asList(new AuthenticationCommand(), new ListCreatingCommand());
    }

    public static DatabaseConnectionService getDatabaseConnectionService() {
        return new DatabaseConnectionServiceImpl();
    }

    public static AuthenticationService getAuthenticationService() {
        return new AuthenticationServiceImpl();
    }

    public static ListOfClientFilesInCloudCreatingService getListOfFilesService() {
        return new ListOfClientFilesInCloudCreatingService();
    }
}
