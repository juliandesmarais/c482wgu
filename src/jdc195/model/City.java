package jdc195.model;

import javafx.util.Pair;
import jdc195.database.QueryConstants;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

public class City extends Queryable {

  private Integer cityId; // PK
  private String city;
  private Integer countryId; // FK

  public City() {
    super();
    table = Tables.CITY;
  }

  public Integer getCityId() {
    return cityId;
  }

  private City setCityId(Integer cityId) {
    this.cityId = cityId;
    return this;
  }

  public String getCity() {
    return city;
  }

  public City setCity(String city) {
    this.city = city;
    return this;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public City setCountryId(Integer countryId) {
    this.countryId = countryId;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public City setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public City setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public City setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public City setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  @Override protected LinkedHashMap<QueryConstants.Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<QueryConstants.Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.CITY, this.city);
    columnsWithValues.put(Columns.COUNTRY_ID, this.countryId);
    return columnsWithValues;
  }

  public static ResultSet getResultsWithCityId(int cityId) throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(Tables.CITY, new Pair<>(Columns.CITY_ID, cityId));
  }
}
