package ru.aberezhnoy.client.factory;

import ru.aberezhnoy.client.core.NetworkService;
import ru.aberezhnoy.client.service.Callback;
import ru.aberezhnoy.client.service.CommandDictionaryService;
import ru.aberezhnoy.client.service.CommandService;
import ru.aberezhnoy.client.service.impl.ClientCommandDictionaryServiceImpl;
import ru.aberezhnoy.client.service.impl.gui_command.AcceptedLoginCommand;
import ru.aberezhnoy.client.service.impl.gui_command.CloudFilesListCommand;
import ru.aberezhnoy.client.service.impl.gui_command.FailedLoginCommand;

import java.util.Arrays;
import java.util.List;

public class Factory {

    public static NetworkService initializeNetworkService(Callback setButtonsAbleCallback) {
        return NettyNetworkService.initializeNetwork(setButtonsAbleCallback);
    }

    public static CommandDictionaryService getCommandDictionary() {
        return new ClientCommandDictionaryServiceImpl();
    }

    public static List<CommandService> getCommandServices () {
        return Arrays.asList(new AcceptedLoginCommand(), new FailedLoginCommand(), new CloudFilesListCommand(), new UploadFileCommand());
    }
}
