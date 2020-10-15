package jdc195.database;

public class QueryConstants {
  public enum Tables {
    ADDRESS,
    APPOINTMENT,
    CITY,
    COUNTRY,
    CUSTOMER,
    USER;

    public String getTableName() {
      return this.name().toLowerCase();
    }
  }

  public enum Columns {
    ADDRESS_ID("addressId"),
    ADDRESS("address"),
    ADDRESS_2("address2"),
    APPOINTMENT_ID("appointmentId"),
    CITY("city"),
    CITY_ID("cityId"),
    COUNTRY("country"),
    COUNTRY_ID("countryId"),
    CONTACT("contact"),
    CUSTOMER_ID("customerId"),
    CUSTOMER_NAME("customerName"),
    DESCRIPTION("description"),
    END("end"),
    LOCATION("location"),
    POSTAL_CODE("postalCode"),
    PHONE("phone"),
    CREATE_DATE("createDate"),
    CREATED_BY("createdBy"),
    LAST_UPDATE("lastUpdate"),
    LAST_UPDATE_BY("lastUpdateBy"),
    START("start"),
    TITLE("title"),
    TYPE("type"),
    URL("url"),
    USER_ID("userId"),
    USER_NAME("userName"),
    PASSWORD("password"),
    ACTIVE("active");

    private final String columnName;

    Columns(String columnName) {
      this.columnName = columnName;
    }

    public String getColumnName() {
      return columnName;
    }
  }
}
