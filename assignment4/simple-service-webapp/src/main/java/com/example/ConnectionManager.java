package com.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Database connection.
 */
public class ConnectionManager {

  private static ComboPooledDataSource ds = new ComboPooledDataSource();

  private final static String USER = "dbuser";
  private final static String PASSWORD = "dbpassword";
  private final static String HOSTNAME = "bsdsassignment2.cjmm694bjqke.us-west-1.rds.amazonaws.com";
  private final static int PORT = 3306;
  private final static String SCHEMA = "StepApp";

  static {
    try {
      ds.setDriverClass("com.mysql.cj.jdbc.Driver");
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
    ds.setJdbcUrl("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + SCHEMA);
    ds.setUser(USER);
    ds.setPassword(PASSWORD);
    ds.setMinPoolSize(1);
    ds.setMaxPoolSize(100);
    ds.setAcquireIncrement(1);
  }

  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  /**
   * Close the connection to the database instance.
   */
  public void closeConnection(Connection connection) throws SQLException {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }

//  /**
//   * Get the connection to the database instance.
//   */
//  public Connection getConnection() throws SQLException {
//    Connection connection;
//    try {
//      try {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//      } catch (ClassNotFoundException ex) {
//        Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
//        throw new SQLException(ex);
//      }
//      connection = DriverManager.getConnection(
//          "jdbc:mysql://bsdsassignment2.cjmm694bjqke.us-west-1.rds.amazonaws.com:3306/StepApp",
//          "dbuser", "dbpassword");
//    } catch (SQLException ex) {
//      Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
//      throw ex;
//    }
//    return connection;
//  }

}
