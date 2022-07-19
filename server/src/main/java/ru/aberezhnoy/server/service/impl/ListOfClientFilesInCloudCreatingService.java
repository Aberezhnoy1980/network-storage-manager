package ru.aberezhnoy.server.service.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.common.domain.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfClientFilesInCloudCreatingService {

    private final Path currentPath = Paths.get(ServerPropertiesReceiver.getCloudDirectory());
    private static final Logger LOGGER = LogManager.getLogger(ListOfClientFilesInCloudCreatingService.class);

    public List<FileInfo> createServerFilesList (String login) {
        try {
            Path userDirectory = currentPath.resolve(login);
            return Files.list(userDirectory).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.throwing(Level.ERROR, e);
            return null;
        }
    }
}
