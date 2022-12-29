package com.skuehnel.dbvisualizer.retrieve;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;

/**
 * @author Stefan Kuehnel
 */
public class JDBCConnection {

    private final String driver;
    private final String driverPath;
    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCConnection.class);

    /**
     * Constructor
     *
     * @param driver     name of the driver class
     * @param driverPath path for driver class (or .jar file)
     * @param url        JDBC url
     * @param user       name of DB user
     * @param password   password of DB user
     */
    public JDBCConnection(String driver, String driverPath, String url, String user, String password) throws ConnectionException {
        this.driver = driver;
        this.driverPath = driverPath;
        this.url = url;
        this.user = user;
        this.password = password;
        if (driver == null || url == null) {
            throw new ConnectionException("At least driver and url must be set.");
        }
        try {
            initialize();
        } catch (Exception e) {
            throw new ConnectionException("Could not initialize database connection.", e);
        }
    }

    /**
     * Get the JDBC connection to the database
     *
     * @return a {@link java.sql.Connection} object
     */
    public Connection getConnection() {
        return connection;
    }

    protected void initialize() throws ClassNotFoundException, MalformedURLException, SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (StringUtils.isNotEmpty(driverPath)) {
            LOGGER.debug("Trying to load JBDC driver from path {}", driverPath);
            URL urls[] = {new File(driverPath).toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class driverClass = Class.forName(driver, false, urlClassLoader);
            LOGGER.debug("Instantiate the driver and connect directly.");
            Driver d = (Driver) driverClass.getDeclaredConstructor().newInstance();
            Properties connectionProperies = new Properties();
            connectionProperies.setProperty("user", user);
            connectionProperies.setProperty("password", password);
            connection = d.connect(url, connectionProperies);

        } else {
            LOGGER.debug("Load driver from classpath and connect by DriverManager.");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        }

    }


}
