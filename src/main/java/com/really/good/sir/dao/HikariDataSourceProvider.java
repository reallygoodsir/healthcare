package com.really.good.sir.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class HikariDataSourceProvider {
    private static final String DB_PROPERTY_FILE = "db.properties";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "db.url";
    private static final String DB_USER = "db.user";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_MAXIMUM_POOL_SIZE = "db.maximum.pool.size";
    private static final String DB_MINIMUM_IDLE = "db.minimum.idle";
    private static final String DB_CONNECTION_TIMEOUT = "db.connection.timeout";
    private static final String DB_IDLE_TIMEOUT = "db.idle.timeout";
    private static final String DB_MAX_LIFETIME = "db.max.lifetime";
    protected static final DataSource DATA_SOURCE;

    static {
        try (InputStream input = HikariDataSourceProvider.class
                .getClassLoader()
                .getResourceAsStream(DB_PROPERTY_FILE)) {

            if (input == null) {
                throw new RuntimeException("Unable to find " + DB_PROPERTY_FILE);
            }

            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty(DB_URL));
            config.setUsername(properties.getProperty(DB_USER));
            config.setPassword(properties.getProperty(DB_PASSWORD));
            config.setDriverClassName(MYSQL_DRIVER);

            // sensible defaults
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty(DB_MAXIMUM_POOL_SIZE)));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty(DB_MINIMUM_IDLE)));
            config.setConnectionTimeout(Integer.parseInt(properties.getProperty(DB_CONNECTION_TIMEOUT)));
            config.setIdleTimeout(Integer.parseInt(properties.getProperty(DB_IDLE_TIMEOUT)));
            config.setMaxLifetime(Integer.parseInt(properties.getProperty(DB_MAX_LIFETIME)));

            DATA_SOURCE = new HikariDataSource(config);
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to initialize HikariCP", exception);
        }
    }
}
