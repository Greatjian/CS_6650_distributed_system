package com.amazonaws.lambda.demo;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    // User to connect to your database instance. By default, this is "root2".
    private final static String MYSQL_USERNAME = "dbuser";
    // Password for the user.
    private final static String MYSQL_PASSWORD = "dbpassword";
    // URI to your database server. If running on the same machine, then this is "localhost".
    private final static String AWS_MYSQL_HOSTNAME = "localhost";
    // Port to your database server. By default, this is 3307.
    private final static int AWS_MYSQL_PORT = 3306;
    // Name of the MySQL schema that contains your tables.
    private final static String AWS_MYSQL_SCHEMA = "StepApp";

    private static DataSource dataSource = setupDataSource();

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static DataSource setupDataSource() {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl("jdbc:mysql://" + AWS_MYSQL_HOSTNAME + ":" + AWS_MYSQL_PORT + "/" + AWS_MYSQL_SCHEMA);
        cpds.setUser(MYSQL_USERNAME);
        cpds.setPassword(MYSQL_PASSWORD);
        cpds.setMinPoolSize(1);
        cpds.setAcquireIncrement(1);
        cpds.setMaxPoolSize(100);
        return cpds;
    }

    /** Close the connection to the database instance. */
    public void closeConnection() throws SQLException {
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
