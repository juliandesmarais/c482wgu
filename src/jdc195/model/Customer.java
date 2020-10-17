package jdc195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Customer extends Queryable {
  private Integer customerId; // PK
  private String customerName;
  private Integer addressId; // FK
  private Boolean active;

  public Customer() {
    super();
    table = Tables.CUSTOMER;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  private Customer setCustomerId(Integer customerId) {
    this.customerId = customerId;
    return this;
  }

  public String getCustomerName() {
    return customerName;
  }

  public Customer setCustomerName(String customerName) {
    this.customerName = customerName;
    return this;
  }

  public Integer getAddressId() {
    return addressId;
  }

  public Customer setAddressId(Integer addressId) {
    this.addressId = addressId;
    return this;
  }

  public Boolean getActive() {
    return active;
  }

  public Customer setActive(Boolean active) {
    this.active = active;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public Customer setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Customer setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public Customer setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public Customer setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  private static ObservableList<Customer> getCustomersWithResultSet(ResultSet resultSet) throws SQLException {
    ObservableList<Customer> customers = FXCollections.observableArrayList();

    while (resultSet.next()) {
      customers.add(new Customer()
          .setActive(resultSet.getBoolean(Columns.ACTIVE.getColumnName()))
          .setAddressId(resultSet.getInt(Columns.ADDRESS_ID.getColumnName()))
          .setCustomerId(resultSet.getInt(Columns.CUSTOMER_ID.getColumnName()))
          .setCustomerName(resultSet.getString(Columns.CUSTOMER_NAME.getColumnName()))
          .setCreateDate(resultSet.getTimestamp(Columns.CREATE_DATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
          .setCreatedBy(resultSet.getString(Columns.CREATED_BY.getColumnName()))
          .setLastUpdate(resultSet.getTimestamp(Columns.LAST_UPDATE.getColumnName()).toInstant().atZone(ZoneId.of("UTC")))
          .setLastUpdateBy(resultSet.getString(Columns.LAST_UPDATE_BY.getColumnName())));
      }
    return customers;
  }

  public static ObservableList<Customer> getCustomersTableData() throws SQLException {
    return getCustomersWithResultSet(new Customer().selectAll());
  }

  public static ResultSet getResultsWithCustomerId(int customerId) throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(Tables.CUSTOMER, new Pair<>(Columns.CUSTOMER_ID, customerId));
  }

  public static Customer getCustomerWithName(String customerName) throws SQLException {
    ResultSet matchingCustomersResultSet = QueryUtility.executeSelectIncludingQuery(Tables.CUSTOMER, new Pair<>(Columns.CUSTOMER_NAME, customerName));
    List<Customer> matchingCustomers = getCustomersWithResultSet(matchingCustomersResultSet);
    return matchingCustomers.size() > 0 ? matchingCustomers.get(0) : null;
  }

  public static String getCustomerNameWithId(int customerId) throws SQLException {
    List<Customer> matchingCustomers = getCustomersWithResultSet(getResultsWithCustomerId(customerId));
    return matchingCustomers.size() > 0 ? matchingCustomers.get(0).getCustomerName() : "";
  }

  @Override protected LinkedHashMap<Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.CUSTOMER_NAME, this.customerName);
    columnsWithValues.put(Columns.ADDRESS_ID, this.addressId);
    columnsWithValues.put(Columns.ACTIVE, this.active);
    return columnsWithValues;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Customer customer = (Customer) o;
    return customerId.equals(customer.customerId) && customerName.equals(customer.customerName) && addressId.equals(customer.addressId) && active.equals(customer.active);
  }

  @Override public int hashCode() {
    return Objects.hash(customerId, customerName, addressId, active, createdBy, lastUpdateBy);
  }

}
