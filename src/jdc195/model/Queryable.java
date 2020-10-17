package jdc195.model;

import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;
import jdc195.support.DateUtility;
import jdc195.support.UserManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

public abstract class Queryable {
  protected ZonedDateTime createDate;
  protected String createdBy;
  protected ZonedDateTime lastUpdate; // Default value in DB is current date time
  protected String lastUpdateBy;
  protected Tables table;

  public Queryable() {
    User currentUser = UserManager.getInstance().getUser();
    if (currentUser != null) {
      this.createdBy = currentUser.getUserName();
      this.lastUpdateBy = currentUser.getUserName();
    }

    createDate = DateUtility.getCurrentSystemDefaultZonedDateTime();
  }

  protected LinkedHashMap<Columns, Object> getUserDataColumnsWithValues() {
    LinkedHashMap<Columns, Object> columnsWithValues = getColumnsWithValuesWithoutUserData();
    columnsWithValues.put(Columns.CREATE_DATE, this.createDate);
    columnsWithValues.put(Columns.CREATED_BY, this.createdBy);
    columnsWithValues.put(Columns.LAST_UPDATE, this.lastUpdate);
    columnsWithValues.put(Columns.LAST_UPDATE_BY, this.lastUpdateBy);
    return columnsWithValues;
  }

  protected LinkedHashMap<Columns, Object> getColumnsWithValuesWithoutUserData() {
    return new LinkedHashMap<>();
  }

  public Integer executeInsert() throws SQLException {
    return QueryUtility.executeInsertIntoQuery(table, getUserDataColumnsWithValues());
  }

  public ResultSet selectAll() throws SQLException {
    return QueryUtility.executeSelectAllQuery(table);
  }

  public ResultSet getCurrentRecordResults() throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(table, getColumnsWithValuesWithoutUserData());
  }

}
