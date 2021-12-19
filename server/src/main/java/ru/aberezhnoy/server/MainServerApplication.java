package ru.aberezhnoy.server;

import ru.aberezhnoy.server.factory.Factory;

public class MainServerApplication {

    public static void main(String[] args) {

        Factory.getServerService().startServer();
    }

}
