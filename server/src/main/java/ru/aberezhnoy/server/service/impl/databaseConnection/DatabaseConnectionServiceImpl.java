package ru.aberezhnoy.server.service.impl.databaseConnection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.server.service.DatabaseConnectionService;
import ru.aberezhnoy.server.service.impl.ServerPropertiesReceiver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionServiceImpl.class);
    private final Connection connection;
    private final Statement stmt;

    public DatabaseConnectionServiceImpl() {
        try {
            Class.forName(ServerPropertiesReceiver.getDbDriver());
            this.connection = DriverManager.getConnection(ServerPropertiesReceiver.getDbURL());
            this.stmt = connection.createStatement();
            this.stmt.execute("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `login` TEXT NOT NULL, `pass` TEXT NOT NULL);");
            LOGGER.info("Сервер подключен к базе данных");
        } catch (ClassNotFoundException | SQLException throwables) {
            LOGGER.throwing(Level.ERROR, throwables);
            throw new RuntimeException("Не удается подключиться к базе данных");
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public Statement getStmt() {
        return stmt;
    }

    @Override
    public void closeConnection() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.throwing(Level.ERROR, e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.throwing(Level.ERROR, e);
            }
        }
    }
}
