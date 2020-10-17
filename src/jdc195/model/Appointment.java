package jdc195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;
import jdc195.support.AlertUtility;
import jdc195.support.DateUtility;
import jdc195.support.UserManager;

import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Appointment extends Queryable {

  private Integer appointmentId; // PK
  private Integer customerId; // FK
  private Integer userId; // FK

  private String title;
  private String description;
  private String location;
  private String contact;
  private String type;
  private String url;
  private ZonedDateTime start;
  private ZonedDateTime end;

  public Appointment() {
    super();
    table = Tables.APPOINTMENT;
  }

  public Integer getAppointmentId() {
    return appointmentId;
  }

  public Appointment setAppointmentId(Integer appointmentId) {
    this.appointmentId = appointmentId;
    return this;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Appointment setCustomerId(Integer customerId) {
    this.customerId = customerId;
    return this;
  }

  public Integer getUserId() {
    return userId;
  }

  public Appointment setUserId(Integer userId) {
    this.userId = userId;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Appointment setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Appointment setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getLocation() {
    return location;
  }

  public Appointment setLocation(String location) {
    this.location = location;
    return this;
  }

  public String getContact() {
    return contact;
  }

  public Appointment setContact(String contact) {
    this.contact = contact;
    return this;
  }

  public String getType() {
    return type;
  }

  public Appointment setType(String type) {
    this.type = type;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public Appointment setUrl(String url) {
    this.url = url;
    return this;
  }

  public ZonedDateTime getStart() {
    return start;
  }

  public Appointment setStart(ZonedDateTime start) {
    this.start = start;
    return this;
  }

  public ZonedDateTime getEnd() {
    return end;
  }

  public Appointment setEnd(ZonedDateTime end) {
    this.end = end;
    return this;
  }

  public ZonedDateTime getCreateDate() {
    return createDate;
  }

  public Appointment setCreateDate(ZonedDateTime createDate) {
    this.createDate = createDate;
    return this;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Appointment setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ZonedDateTime getLastUpdate() {
    return lastUpdate;
  }

  public Appointment setLastUpdate(ZonedDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  public String getLastUpdateBy() {
    return lastUpdateBy;
  }

  public Appointment setLastUpdateBy(String lastUpdateBy) {
    this.lastUpdateBy = lastUpdateBy;
    return this;
  }

  @Override protected LinkedHashMap<Columns, Object> getColumnsWithValuesWithoutUserData() {
    LinkedHashMap<Columns, Object> columnsWithValues = new LinkedHashMap<>();
    columnsWithValues.put(Columns.APPOINTMENT_ID, this.appointmentId);
    columnsWithValues.put(Columns.CUSTOMER_ID, this.customerId);
    columnsWithValues.put(Columns.TITLE, this.title);
    columnsWithValues.put(Columns.DESCRIPTION, this.description);
    columnsWithValues.put(Columns.LOCATION, this.location);
    columnsWithValues.put(Columns.CONTACT, this.contact);
    columnsWithValues.put(Columns.TYPE, this.type);
    columnsWithValues.put(Columns.URL, this.url);
    columnsWithValues.put(Columns.START, this.start);
    columnsWithValues.put(Columns.END, this.end);
    columnsWithValues.put(Columns.USER_ID, this.userId);
    return columnsWithValues;
  }

  public enum AppointmentsTableDataFilter {
    ALL, CURRENT_MONTH, CURRENT_WEEK
  }

  public static ObservableList<Appointment> getAppointmentsTableData(AppointmentsTableDataFilter filter) throws SQLException {
    ResultSet resultSet = new Appointment().selectAll();

    ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    while (resultSet.next()) {
      appointments.add(new Appointment()
          .setAppointmentId(resultSet.getInt(Columns.APPOINTMENT_ID.getColumnName()))
          .setCustomerId(resultSet.getInt(Columns.CUSTOMER_ID.getColumnName()))
          .setUserId(resultSet.getInt(Columns.USER_ID.getColumnName()))
          .setTitle(resultSet.getString(Columns.TITLE.getColumnName()))
          .setDescription(resultSet.getString(Columns.DESCRIPTION.getColumnName()))
          .setLocation(resultSet.getString(Columns.LOCATION.getColumnName()))
          .setContact(resultSet.getString(Columns.CONTACT.getColumnName()))
          .setType(resultSet.getString(Columns.TYPE.getColumnName()))
          .setUrl(resultSet.getString(Columns.URL.getColumnName()))
          .setStart(DateUtility.convertLocalDateTimeToSystemDefaultZDT((resultSet.getObject(Columns.START.getColumnName(), LocalDateTime.class))))
          .setEnd(DateUtility.convertLocalDateTimeToSystemDefaultZDT((resultSet.getObject(Columns.END.getColumnName(), LocalDateTime.class))))
          .setCreateDate(DateUtility.convertLocalDateTimeToSystemDefaultZDT((resultSet.getObject(Columns.CREATE_DATE.getColumnName(), LocalDateTime.class))))
          .setLastUpdate(DateUtility.convertLocalDateTimeToSystemDefaultZDT((resultSet.getObject(Columns.LAST_UPDATE.getColumnName(), LocalDateTime.class))))
          .setCreatedBy(resultSet.getString(Columns.CREATED_BY.getColumnName()))
          .setLastUpdateBy(resultSet.getString(Columns.LAST_UPDATE_BY.getColumnName())));
    }

    switch (filter) {
    case CURRENT_WEEK:
      // Requirement G:1 - Lambda is useful in this case as a multiple statement loop is reduced to a single line
      // Function is filtering the appointment data set to reduce it to the set that has the same week of year as the current week of year
      appointments = appointments.filtered(c -> DateUtility.getWeekOfYearFromLocalDateTime(c.getStart().toLocalDateTime()) == DateUtility.getWeekOfYearFromLocalDateTime(DateUtility.getCurrentSystemDefaultLocalDateTime()));
      break;
    case CURRENT_MONTH:
      // Requirement G:2 - Lambda is useful in this case as a multiple statement loop is reduced to a single line
      // Function is filtering the appointment data set to reduce it to the set that has the same month and the same year as the current month and year
      appointments = appointments.filtered(c -> c.getStart().getMonth() == DateUtility.getCurrentSystemDefaultLocalDateTime().getMonth() && c.getStart().getYear() == DateUtility.getCurrentSystemDefaultLocalDateTime().getYear());
      break;
    default:
      break;
    }

    return appointments;
  }

  public static ResultSet getResultsWithAppointmentId(int appointmentId) throws SQLException {
    return QueryUtility.executeSelectIncludingQuery(Tables.APPOINTMENT, new Pair<>(Columns.APPOINTMENT_ID, appointmentId));
  }

  public static void checkLoginTimeWithinAppointmentWindow() throws SQLException {
    ResultSet allAppointments = new Appointment().selectAll();
    LocalDateTime currentTime = DateUtility.getCurrentZonedDateTimeInUTC().toLocalDateTime();

    while (allAppointments.next()) {
      LocalDateTime appointmentTime = allAppointments.getObject(Columns.START.getColumnName(), LocalDateTime.class);
      long minutesBetween = ChronoUnit.MINUTES.between(currentTime, appointmentTime);
      boolean sameUser = UserManager.getInstance().getUser().getUserId() == allAppointments.getInt(Columns.USER_ID.getColumnName());

      if (sameUser && (minutesBetween >= -15 && minutesBetween <= 15)) {
        AlertUtility.displayAlert(Alert.AlertType.WARNING, null, "Appointment", "There is an appointment within 15 minutes.");
      }
    }
  }

  public boolean isStartAfterEnd() {
    LocalDateTime startTime = getStart().toLocalDateTime();
    LocalDateTime endTime = getEnd().toLocalDateTime();

    if (startTime.isEqual(endTime) || startTime.isAfter(endTime)) {
      AlertUtility.displayErrorAlert("Invalid Appointment Time", String.format("Appointment start time must be before appointment end time."));
      return false;
    }

    return true;
  }

  public boolean isWithinBusinessHours() {
    LocalDateTime startTime = getStart().toLocalDateTime();
    LocalDateTime endTime = getEnd().toLocalDateTime();

    boolean startTimeValid = startTime.getHour() >= 9 && startTime.getHour() <= 18;
    boolean endTimeValid = endTime.getHour() >= 9 && endTime.getHour() <= 18;

    if (!startTimeValid || !endTimeValid) {
      AlertUtility.displayErrorAlert("Invalid Appointment Time", String.format("Appointment hours must be set within business hours of 9AM and 6PM"));
      return false;
    }

    return true;
  }

  public boolean isValidAppointmentTime() throws SQLException {
    ResultSet allAppointments = selectAll();
    LocalDateTime apptStart = getStart().toLocalDateTime();
    LocalDateTime apptEnd = getEnd().toLocalDateTime();

    while (allAppointments.next()) {
      boolean sameUser = getUserId() == allAppointments.getInt(Columns.USER_ID.getColumnName());
      if (getAppointmentId() == null || allAppointments.getInt(Columns.APPOINTMENT_ID.getColumnName()) != getAppointmentId()) {

        LocalDateTime currentStart = DateUtility.convertLocalDateTimeToSystemDefaultLDT(allAppointments.getObject(Columns.START.getColumnName(), LocalDateTime.class));
        LocalDateTime currentEnd = DateUtility.convertLocalDateTimeToSystemDefaultLDT(allAppointments.getObject(Columns.END.getColumnName(), LocalDateTime.class));

        boolean startTimeBetween = apptStart.isAfter(currentStart) && apptStart.isBefore(currentEnd);
        boolean endTimeBetween = apptEnd.isAfter(currentStart) && apptEnd.isBefore(currentEnd);

        // Start or end time cannot be in between any appointments
        if ((startTimeBetween || endTimeBetween) && sameUser) {
          String failureMessage = ("Appointment start or end time cannot be in between any existing appointment time."
                        + String.format("\n\nIntersected appointment time: \nStart: %s \nEnd: %s", currentStart, currentEnd));

          AlertUtility.displayErrorAlert("Invalid Appointment Time", failureMessage);
          return false;
        }

        // Outside of bounds of appointment, make sure that start and end don't intersect
        boolean beforeValid = apptStart.isBefore(currentStart) && (apptEnd.isBefore(currentStart) || apptEnd.isEqual(currentStart));
        boolean afterValid = (apptStart.isAfter(currentEnd) || apptStart.isEqual(currentEnd)) && apptEnd.isAfter(currentEnd);

        if ((!beforeValid && !afterValid) && sameUser) {
          String failureMessage = ("Appointment must begin and end outside of any existing appointment time."
                        + String.format("\n\nExisting Appointment: \nStart: %s \nEnd: %s", currentStart, currentEnd));

          AlertUtility.displayErrorAlert("Invalid Appointment Time", failureMessage);
          return false;
        }
      }
    }

    return true;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Appointment that = (Appointment) o;
    return Objects.equals(appointmentId, that.appointmentId) && Objects.equals(customerId, that.customerId) && Objects.equals(userId, that.userId) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(location, that.location) && Objects.equals(contact, that.contact) && Objects.equals(type, that.type) && Objects.equals(url, that.url) && Objects
        .equals(start, that.start) && Objects.equals(end, that.end);
  }

  @Override public int hashCode() {
    return Objects.hash(appointmentId, customerId, userId, title, description, location, contact, type, url, start, end);
  }
}
