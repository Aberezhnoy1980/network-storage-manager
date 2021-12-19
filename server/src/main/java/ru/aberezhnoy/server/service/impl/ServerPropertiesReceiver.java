package ru.aberezhnoy.server.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerPropertiesReceiver {

    private static final String pathToProperties = "server/src/main/resources/server.properties";
    private static final Properties properties = new Properties();

    private static String getProperties(String propertyName) {
        try (InputStream in = new FileInputStream(pathToProperties)) {
            properties.load(in);
            return properties.getProperty(propertyName);
        } catch (IOException e) {
            throw new IllegalArgumentException("Значение " + propertyName + " отсутствует");
        }
    }

    public static int getPort() {
        return Integer.parseInt(ServerPropertiesReceiver.getProperties("port").trim());
    }

    public static String getCloudDirectory() {
        return ServerPropertiesReceiver.getProperties("cloudDirectory");
    }

    public static String getDbDriver() {
        return ServerPropertiesReceiver.getProperties("dbDriver");
    }

    public static String getDbURL() {
        return ServerPropertiesReceiver.getProperties("dbURL");
    }
}
