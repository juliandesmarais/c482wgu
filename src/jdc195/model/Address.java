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

public class Address extends Queryable {

  private Integer addressId; // PK
  private String address;
  private String address2;
  private Integer cityId; // FK
  private String postalCode;
  private String phone;

  public Address() {
    super();
    table = Tables.ADDRESS;
  }

  public Integer getAddressId() {
    return addressId;
  }

  private Address setAddressId(Integer addressId) {
    this.addressId = addressId;
    return this;
  }

  public String getAddress() {
    return address;
  }

  public Address setAddress(String address) {
    this.address = address;
    return this;
  }

  public String getAddress2() {
    return address2;
  }

  public Address setAddress2(String address2) {
    this.address2 = address2;
    return this;
  }

  public Integer getCityId() {
    return cityId;
  }

  public Address setCityId(Integer cityId) {
    this.cityId = cityId;
    return this;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public Address setPostalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  public String getPhone() {
    return phone;
  }

  public Address setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public Address setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Address setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public Address setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public Address setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  @Override protected LinkedHashMap<QueryConstants.Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<QueryConstants.Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.ADDRESS, this.address);
    columnsWithValues.put(Columns.ADDRESS_2, this.address2);
    columnsWithValues.put(Columns.CITY_ID, this.cityId);
    columnsWithValues.put(Columns.POSTAL_CODE, this.postalCode);
    columnsWithValues.put(Columns.PHONE, this.phone);
    return columnsWithValues;
  }

  public static ResultSet getResultsWithAddressId(int addressId) throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(Tables.ADDRESS, new Pair<>(Columns.ADDRESS_ID, addressId));
  }

}
