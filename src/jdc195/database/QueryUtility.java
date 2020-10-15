package jdc195.database;

import javafx.util.Pair;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class QueryUtility {

  public static ResultSet executeSelectAllQuery(Tables table) throws SQLException {
    String statement = String.format("SELECT * FROM %s", table.name().toLowerCase());
    return ConnectionManager.getInstance().createStatement().executeQuery(statement);
  }

  public static <T> ResultSet executeSelectIncludingQuery(Tables table, Pair<Columns, T> filter) throws SQLException {
    String statement = String.format("SELECT * FROM %s WHERE %s=?", table.name().toLowerCase(), filter.getKey().getColumnName());
    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(statement);
    sqlStatement.setObject(1,  filter.getValue());

    return sqlStatement.executeQuery();
  }

  public static <T> ResultSet executeSelectIncludingQuery(Tables table, LinkedHashMap<Columns, T> filter) throws SQLException {
    StringBuilder filterBuilder = new StringBuilder();
    List<Columns> columnKeys = new ArrayList<>(filter.keySet());

    for (int i = 0; i < columnKeys.size(); i++) {
      filterBuilder.append(columnKeys.get(i).getColumnName());
      filterBuilder.append("=?");

      if (i != (columnKeys.size() - 1)) {
        filterBuilder.append(" AND ");
      }
    }

    String initialQuery = String.format("SELECT * FROM %s WHERE %s", table.toString().toLowerCase(), filterBuilder.toString());
    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(initialQuery, Statement.RETURN_GENERATED_KEYS);
    int valuesParameterIndex = 1;

    for (T value : filter.values()) {
      if (value instanceof ZonedDateTime) {
        sqlStatement.setObject(valuesParameterIndex,  convertValueForDatabaseInsertion(value));
      } else {
        sqlStatement.setObject(valuesParameterIndex, value);
      }

      valuesParameterIndex += 1;
    }

    return sqlStatement.executeQuery();
  }

  public static <T> ResultSet executeSelectExcludingQuery(Tables table, Pair<Columns, T> filter) throws SQLException {
    String statement = String.format("SELECT * FROM %s WHERE %s<>?", table.name().toLowerCase(), filter.getKey().getColumnName());
    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(statement);
    sqlStatement.setObject(1,  filter.getValue());

    return sqlStatement.executeQuery();
  }

  // RETURNS KEY
  public static <T> Integer executeInsertIntoQuery(Tables table, LinkedHashMap<Columns, T> columnsWithValues) throws SQLException, ClassCastException {
    StringBuilder columnsBuilder = new StringBuilder();
    StringBuilder valuesParameter = new StringBuilder();
    List<Columns> columnKeys = new ArrayList<>(columnsWithValues.keySet());

    for (int i = 0; i < columnKeys.size(); i++) {
      columnsBuilder.append(columnKeys.get(i).getColumnName());
      valuesParameter.append("?");

      if (i != (columnKeys.size() - 1)) {
        columnsBuilder.append(",");
        valuesParameter.append(",");
      }
    }

    String initialQuery = String.format("INSERT INTO %s (%s) VALUES (%s)",
        table.toString().toLowerCase(), columnsBuilder.toString(), valuesParameter.toString());

    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(initialQuery, Statement.RETURN_GENERATED_KEYS);

    int valuesParameterIndex = 1;

    for (T value : columnsWithValues.values()) {
      if (value instanceof ZonedDateTime) {
        sqlStatement.setObject(valuesParameterIndex,  convertValueForDatabaseInsertion(value));
      } else {
        sqlStatement.setObject(valuesParameterIndex, value);
      }

      valuesParameterIndex += 1;
    }

    sqlStatement.executeUpdate();
    ResultSet resultSet = sqlStatement.getGeneratedKeys();
    if (resultSet.next()) {
      return resultSet.getInt(1);
    }

    return 0;
  }

  // RETURNS KEY
  public static Integer executeDelete(Tables table, Pair<Columns, Object> filter) throws SQLException {
    String statement = String.format("DELETE FROM %s WHERE %s=?", table.getTableName(), filter.getKey().getColumnName());
    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

    if (filter.getValue() instanceof ZonedDateTime) {
      sqlStatement.setObject(1, convertValueForDatabaseInsertion(filter.getValue()));
    } else {
      sqlStatement.setObject(1, filter.getValue());
    }

    sqlStatement.executeUpdate();
    ResultSet resultSet = sqlStatement.getGeneratedKeys();
    if (resultSet.next()) {
      return resultSet.getInt(1);
    }

    return 0;
  }

  // RETURNS KEY
  public static <T> Integer executeUpdateQuery(Tables table, Pair<Columns, T> set, Pair<Columns, T> filter) throws SQLException {
    String statement = String.format("UPDATE %s SET %s=? WHERE %s=?", table.getTableName(), set.getKey().getColumnName(), filter.getKey().getColumnName());
    PreparedStatement sqlStatement = ConnectionManager.getInstance().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);

    if (set.getValue() instanceof ZonedDateTime) {
      sqlStatement.setObject(1, convertValueForDatabaseInsertion(set.getValue()));
    } else {
      sqlStatement.setObject(1, set.getValue());
    }

    if (filter.getValue() instanceof ZonedDateTime) {
      sqlStatement.setObject(2,  convertValueForDatabaseInsertion(filter.getValue()));
    } else {
      sqlStatement.setObject(2, filter.getValue());
    }

    sqlStatement.executeUpdate();
    ResultSet resultSet = sqlStatement.getGeneratedKeys();
    if (resultSet.next()) {
      return resultSet.getInt(1);
    }

    return 0;
  }

  private static LocalDateTime convertValueForDatabaseInsertion(Object value) {
    ZonedDateTime converted = (ZonedDateTime) value;
    return converted.toLocalDateTime();
  }
}
