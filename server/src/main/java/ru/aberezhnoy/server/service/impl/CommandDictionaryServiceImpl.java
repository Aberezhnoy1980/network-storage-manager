package ru.aberezhnoy.server.service.impl;

import ru.aberezhnoy.common.domain.Command;
import ru.aberezhnoy.server.factory.Factory;
import ru.aberezhnoy.server.service.CommandDictionaryService;
import ru.aberezhnoy.server.service.CommandService;

import java.util.*;

public class CommandDictionaryServiceImpl implements CommandDictionaryService {

    private final Map<String, CommandService> commandDictionary;


    public CommandDictionaryServiceImpl() {
        this.commandDictionary = Collections.unmodifiableMap(getCommandDictionary());
    }

    private Map<String, CommandService> getCommandDictionary() {
        List<CommandService> commandServices = Factory.getCommandServices();
        Map<String, CommandService> commandDictionary = new HashMap<>();
        for (CommandService commandService : commandServices) {
            commandDictionary.put(commandService.getCommand(), commandService);
        }
        return commandDictionary;
    }

    @Override
    public Object processCommand(Command command) {

        if (commandDictionary.containsKey(command.getCommandName())) {
            return commandDictionary.get(command.getCommandName()).processCommand(command);
        }
        return null;
    }
}
