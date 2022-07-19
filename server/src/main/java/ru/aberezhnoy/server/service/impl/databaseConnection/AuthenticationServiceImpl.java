package ru.aberezhnoy.server.service.impl.databaseConnection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.aberezhnoy.server.core.NettyServerService;
import ru.aberezhnoy.server.service.AuthenticationService;
import ru.aberezhnoy.server.service.DatabaseConnectionService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final DatabaseConnectionService dbConnection;
    private PreparedStatement registrationSearching;
    private PreparedStatement loginChecking;
    private PreparedStatement newClientCreation;

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl() {
        this.dbConnection = NettyServerService.getDatabaseConnectionService();
        createPreparedStatements();
    }

    public void createPreparedStatements() {
        try {
            this.registrationSearching = dbConnection.getConnection().prepareStatement("select id from users where login = ? and pass = ?;");
            this.loginChecking = dbConnection.getConnection().prepareStatement("select id from users where login = ?;");
            this.newClientCreation = dbConnection.getConnection().prepareStatement("insert into users (login, pass) values (?,?);");
        } catch (SQLException throwables) {
            LOGGER.throwing(Level.ERROR, throwables);
        }
    }

    @Override
    public boolean isClientRegistered(String login, String password) {
        try {
            registrationSearching.setString(1, login);
            registrationSearching.setString(2, password);

            try (ResultSet rs = registrationSearching.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            LOGGER.throwing(Level.ERROR, throwables);
        }
        return false;
    }

    @Override
    public boolean isLoginBusy(String login) {
        try {
            loginChecking.setString(1, login);
            try (ResultSet rs = loginChecking.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            LOGGER.throwing(Level.ERROR, throwables);
        }
        return false;
    }

    @Override
    public boolean registerClient(String login, String password) {
        if (!isClientRegistered(login, password) && !isLoginBusy(login)) {
            try {
                newClientCreation.setString(1, login);
                newClientCreation.setString(2, password);
                newClientCreation.executeUpdate();
                return true;
            } catch (SQLException e) {
                LOGGER.throwing(Level.ERROR, e);
            }
        }
        else if(isLoginBusy(login)) {
            LOGGER.info("Логин занят");
        }
        return false;
    }
}
