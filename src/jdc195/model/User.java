package jdc195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import jdc195.database.ConnectionManager;
import jdc195.database.QueryConstants.*;
import jdc195.database.QueryUtility;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class User extends Queryable {

  private Integer userId; // PK
  private String userName;
  private String password;
  private boolean active;

  public User() {
    super();
    table = Tables.USER;
  }

  public String getUserName() {
    return userName;
  }

  public User setUserName(String userName) {
    this.userName = userName;
    return this;
  }

  public Integer getUserId() {
    return userId;
  }

  public User setUserId(Integer userId) {
    this.userId = userId;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public boolean isActive() {
    return active;
  }

  public User setActive(boolean active) {
    this.active = active;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public User setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public User setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public User setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public User setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  public static List<User> getAllUsers() {
    try {
      return getUsersWithResultSet(selectAll());
    } catch (SQLException e) {
      return new ArrayList<>();
    }
  }

  public static ResultSet selectAll() throws SQLException {
    return QueryUtility.executeSelectAllQuery(Tables.USER);
  }

  private static List<User> getUsersWithResultSet(ResultSet resultSet) throws SQLException {
    List<User> users = new ArrayList<>();

    while (resultSet.next()) {
      users.add(new User()
          .setActive(resultSet.getBoolean(Columns.ACTIVE.getColumnName()))
          .setUserId(resultSet.getInt(Columns.USER_ID.getColumnName()))
          .setUserName(resultSet.getString(Columns.USER_NAME.getColumnName()))
          .setCreateDate(resultSet.getTimestamp(Columns.CREATE_DATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
          .setCreatedBy(resultSet.getString(Columns.CREATED_BY.getColumnName()))
          .setLastUpdate(resultSet.getTimestamp(Columns.LAST_UPDATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
          .setLastUpdateBy(resultSet.getString(Columns.LAST_UPDATE_BY.getColumnName())));
    }

    return users;
  }

  public static User getUserWithCredentials(String userName, String password) {
    User foundUser = null;

    try {
      String statement = "SELECT * FROM user WHERE userName=? AND password=?";
      PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(statement);
      sqlStatement.setString(1, userName);
      sqlStatement.setString(2, password);
      ResultSet foundUsers = sqlStatement.executeQuery();

      if (foundUsers.next()) {
        foundUser = new User()
            .setUserName(foundUsers.getString(Columns.USER_NAME.getColumnName()))
            .setUserId(foundUsers.getInt(Columns.USER_ID.getColumnName()))
            .setActive(foundUsers.getInt(Columns.ACTIVE.getColumnName()) == 1)
            .setCreateDate(foundUsers.getTimestamp(Columns.CREATE_DATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
            .setCreatedBy(foundUsers.getString(Columns.CREATED_BY.getColumnName()))
            .setLastUpdate(foundUsers.getTimestamp(Columns.LAST_UPDATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
            .setLastUpdateBy(foundUsers.getString(Columns.LAST_UPDATE_BY.getColumnName()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return foundUser;
  }

  @Override public LinkedHashMap<Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.USER_NAME, this.userName);
    columnsWithValues.put(Columns.PASSWORD, this.password);
    columnsWithValues.put(Columns.ACTIVE, this.active ? 1 : 0);
    return columnsWithValues;
  }

  @Override public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("User ID: ").append(userId).append("\n")
        .append("User Name: ").append(userName).append("\n")
        .append("Active: ").append(active ? "Yes" : "No");
    return stringBuilder.toString();
  }

  public static User getUserWithUserName(String userName) {
    try {
      List<User> foundUsers = getUsersWithResultSet(QueryUtility.executeSelectIncludingQuery(Tables.USER, new Pair<>(Columns.USER_NAME, userName)));
      if (foundUsers != null && foundUsers.size() > 0) {
        return foundUsers.get(0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}
