package com.really.good.sir.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class BaseDao {
    private static final String DB_PROPERTY_FILE = "db.properties";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    private static final Logger LOGGER = LogManager.getLogger(BaseDao.class);

    static {
        try (final InputStream input = BaseDao.class.getClassLoader()
                .getResourceAsStream(DB_PROPERTY_FILE)) {
            Class.forName(DRIVER);
            if (input == null) {
                throw new RuntimeException("Unable to find " + DB_PROPERTY_FILE + " file");
            }

            final Properties properties = new Properties();
            properties.load(input);

            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
            if (URL == null || URL.isEmpty()) {
                throw new RuntimeException("URL is empty");
            }
            if (USER == null || USER.isEmpty()) {
                throw new RuntimeException("URL is empty");
            }
            if (PASSWORD == null || PASSWORD.isEmpty()) {
                throw new RuntimeException("URL is empty");
            }
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to load database properties", exception);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Failed to close connection", e);
            }
        }
    }
}
