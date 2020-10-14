package jdc195.model;

import javafx.util.Pair;
import jdc195.database.QueryConstants.*;
import jdc195.database.QueryUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

public class Country extends Queryable {

  private Integer countryId; // PK
  private String country;

  public Country() {
    super();
    table = Tables.COUNTRY;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public String getCountry() {
    return country;
  }

  public Country setCountry(String country) {
    this.country = country;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public Country setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Country setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public Country setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public Country setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  public static Integer getCountryIdWithName(String countryName) {
    switch (countryName) {
    case "United States":
      return 1;
    case "Canada":
      return 2;
    case "Japan":
      return 3;
    case "Mexico":
      return 4;
    case "France":
      return 5;
    default:
      throw new IllegalArgumentException(String.format("Country %s is not supported.", countryName));
    }
  }

  @Override public LinkedHashMap<Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.COUNTRY, this.country);
    return columnsWithValues;
  }

  public static ResultSet getResultsWithCountryId(int countryId) throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(Tables.COUNTRY, new Pair<>(Columns.COUNTRY_ID, countryId));
  }
}
