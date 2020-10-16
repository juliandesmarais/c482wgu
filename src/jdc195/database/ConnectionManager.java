package jdc195.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

  private static Connection instance;

  static {
    instance = getMySQLDatabaseConnection();
  }

  private ConnectionManager() {}

  private static Connection getMySQLDatabaseConnection() {
    String serverName = "wgudb.ucertify.com";
    String databaseName = "U05zQK";
    String username = "U05zQK";
    String password = "53688654494";

    String connectionString = "jdbc:mysql://"  + serverName +  ":3306/" + databaseName + "?user=" + username + "&password=" + password;

    try {
      return DriverManager.getConnection(connectionString);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static synchronized Connection getInstance() {
    if (instance == null) {
      instance = getMySQLDatabaseConnection();
    }

    return instance;
  }
}