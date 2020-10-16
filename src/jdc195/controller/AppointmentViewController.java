package jdc195.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;
import jdc195.database.QueryConstants;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryUtility;
import jdc195.model.Appointment;
import jdc195.model.Customer;
import jdc195.model.User;
import jdc195.support.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentViewController implements Initializable {

  @FXML private TextField userField;
  @FXML private Label appointmentViewTitleLabel;
  @FXML private ComboBox<String> customerField;
  @FXML private TextField titleField;
  @FXML private TextField descriptionField;
  @FXML private TextField locationField;
  @FXML private TextField contactField;
  @FXML private TextField typeField;
  @FXML private TextField urlField;
  @FXML private DatePicker startDateField;
  @FXML private ComboBox<String> startTimeHourField;
  @FXML private ComboBox<String> startTimeMinuteField;
  @FXML private RadioButton startTimeAmButton;
  @FXML private RadioButton startTimePmButton;
  @FXML private DatePicker endDateField;
  @FXML private ComboBox<String> endTimeHourField;
  @FXML private ComboBox<String> endTimeMinuteField;
  @FXML private RadioButton endTimeAmButton;
  @FXML private RadioButton endTimePmButton;

  private Appointment savedAppointment = null;

  @Override public void initialize(URL location, ResourceBundle resources) {
    try {
      setupCustomerComboBoxField();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    startTimeHourField.getSelectionModel().select("12");
    startTimeMinuteField.getSelectionModel().select("00");
    startTimePmButton.setSelected(true);

    endTimeHourField.getSelectionModel().select("12");
    endTimeMinuteField.getSelectionModel().select("00");
    endTimePmButton.setSelected(true);

    startDateField.setValue(LocalDate.now());
    endDateField.setValue(LocalDate.now());

    userField.setText(UserManager.getInstance().getUser().getUserName());
    userField.setDisable(true);
  }

  private void setupCustomerComboBoxField() throws SQLException {
    ObservableList<String> customers = FXCollections.observableArrayList();

    for (Customer customer : Customer.getCustomersTableData()) {
      customers.add(customer.getCustomerName());
    }

    customerField.setItems(customers);
    customerField.getSelectionModel().selectFirst();
  }

  public void setAppointment(Appointment appointment) {
    try {
      ResultSet existingAppointment = Appointment.getResultsWithAppointmentId(appointment.getAppointmentId());
      if (existingAppointment.next()) {
        titleField.setText(existingAppointment.getString(Columns.TITLE.getColumnName()));
        descriptionField.setText(existingAppointment.getString(Columns.TITLE.getColumnName()));
        locationField.setText(existingAppointment.getString(Columns.LOCATION.getColumnName()));
        contactField.setText(existingAppointment.getString(Columns.CONTACT.getColumnName()));
        typeField.setText(existingAppointment.getString(Columns.TYPE.getColumnName()));
        urlField.setText(existingAppointment.getString(Columns.URL.getColumnName()));

        setTimeFields(existingAppointment.getObject(Columns.START.getColumnName(), LocalDateTime.class), TimeFieldType.START);
        setTimeFields(existingAppointment.getObject(Columns.END.getColumnName(), LocalDateTime.class), TimeFieldType.END);
      }

      ResultSet existingCustomer = Customer.getResultsWithCustomerId(appointment.getCustomerId());
      if (existingCustomer.next()) {
        customerField.getSelectionModel().select(existingCustomer.getString(Columns.CUSTOMER_NAME.getColumnName()));
      } else {
        AlertUtility.displayErrorAlert("Customer Not Found", "The customer associated with this appointment has been deleted.");
      }

      savedAppointment = appointment;
      appointmentViewTitleLabel.setText("Edit Appointmnet");

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private enum TimeFieldType {
    START, END
  }

  private void setTimeFields(LocalDateTime localDateTime, TimeFieldType startOrEnd) {
    boolean setStartTime = startOrEnd == TimeFieldType.START;

    ComboBox<String> hourField = setStartTime ? startTimeHourField : endTimeHourField;
    ComboBox<String> minuteField = setStartTime ? startTimeMinuteField : endTimeMinuteField;
    RadioButton pmButton = setStartTime ? startTimePmButton : endTimePmButton;
    RadioButton amButton = setStartTime ? startTimeAmButton : endTimeAmButton;
    DatePicker datePicker = setStartTime ? startDateField : endDateField;

    ZonedDateTime converted = DateUtility.convertLocalDateTimeToSystemDefaultZDT(localDateTime);
    datePicker.setValue(converted.toLocalDate());

    int hour = converted.getHour();
    boolean pm = hour >= 12;
    int convertedHour = pm && hour != 12 ? converted.getHour() - 12 : converted.getHour();

    if (!pm && convertedHour == 0) {
      convertedHour = 12;
    }

    hourField.getSelectionModel().select(String.valueOf(convertedHour));
    minuteField.getSelectionModel().select(converted.getMinute());

    if (pm) {
      pmButton.setSelected(true);
    } else {
      amButton.setSelected(true);
    }
  }

  private boolean isRequiredFieldsFilled() {
    List<TextField> textFields = new ArrayList<>();
    textFields.add(titleField);
    textFields.add(descriptionField);
    textFields.add(locationField);
    textFields.add(contactField);
    textFields.add(typeField);
    textFields.add(urlField);
    return TextFieldChecker.validateTextFieldContents(textFields);
  }

  private LocalTime getComboBoxTime(TimeFieldType startOrEnd) {
    boolean setStartTime = startOrEnd == TimeFieldType.START;

    ComboBox<String> hourField = setStartTime ? startTimeHourField : endTimeHourField;
    ComboBox<String> minuteField = setStartTime ? startTimeMinuteField : endTimeMinuteField;
    RadioButton pmButton = setStartTime ? startTimePmButton : endTimePmButton;

    int hour = Integer.parseInt(hourField.getValue());
    int minute = Integer.parseInt(minuteField.getValue());
    boolean pm = pmButton.isSelected();

    if (pm && hour != 12) {
      hour += 12;
    } else if (!pm && hour == 12) {
      hour = 0;
    }

    return LocalTime.of(hour, minute);
  }

  private ZonedDateTime getCurrentStartDate() {
    LocalTime startTime = getComboBoxTime(TimeFieldType.START);
    LocalDateTime startDateTime = LocalDateTime.of(startDateField.getValue(), startTime);

    return ZonedDateTime.of(startDateTime, ZoneId.systemDefault());

    // TODO: There is an open issue with the connector where LocalDateTime values are being converted
    //    ZonedDateTime local =  ZonedDateTime.of(startDateTime, ZoneId.systemDefault());
    //    return ZonedDateTime.ofInstant(local.toInstant(), ZoneId.of("UTC"));
  }

  private ZonedDateTime getCurrentEndDate() {
    LocalTime endTime = getComboBoxTime(TimeFieldType.END);
    LocalDateTime endDateTime = LocalDateTime.of(endDateField.getValue(), endTime);

    return ZonedDateTime.of(endDateTime, ZoneId.systemDefault());

    // TODO: There is an open issue with the connector where LocalDateTime values are being converted
    //    ZonedDateTime local =  ZonedDateTime.of(endDateTime, ZoneId.systemDefault());
    //    return ZonedDateTime.ofInstant(local.toInstant(), ZoneId.of("UTC"));
  }

  private Integer getCurrentCustomerId() throws SQLException {
    String currentCustomerName = customerField.getSelectionModel().getSelectedItem();
    return Customer.getCustomerWithName(currentCustomerName).getCustomerId();
  }

  private Appointment getCurrentAppointment() throws SQLException {
    User currentUser = UserManager.getInstance().getUser();

    return new Appointment()
        .setCustomerId(getCurrentCustomerId())
        .setUserId(currentUser.getUserId())
        .setTitle(titleField.getText())
        .setDescription(descriptionField.getText())
        .setLocation(locationField.getText())
        .setContact(contactField.getText())
        .setType(typeField.getText())
        .setUrl(urlField.getText())
        .setStart(getCurrentStartDate())
        .setEnd(getCurrentEndDate());
  }

  public void handleSaveButton(ActionEvent event) throws SQLException {
    Boolean currentAppointmentUpdated = false;

    if (isRequiredFieldsFilled()) {
      Appointment appointment = getCurrentAppointment();

      if (savedAppointment != null) {
        appointment.setAppointmentId(savedAppointment.getAppointmentId());
      }

      if (!appointment.isStartAfterEnd() || !appointment.isWithinBusinessHours() || !appointment.isValidAppointmentTime()) {
        return;
      }

      if (savedAppointment != null && !savedAppointment.equals(appointment)) {
        Pair<Columns, Object> filter = new Pair<>(Columns.APPOINTMENT_ID, savedAppointment.getAppointmentId());
        QueryConstants.Tables table = QueryConstants.Tables.APPOINTMENT;

        if (!savedAppointment.getCustomerId().equals(appointment.getCustomerId())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.CUSTOMER_ID, appointment.getCustomerId()), filter);
        }
        if (!savedAppointment.getUserId().equals(appointment.getUserId())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.USER_ID, appointment.getUserId()), filter);
        }
        if (!savedAppointment.getTitle().equals(appointment.getTitle())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.TITLE, appointment.getTitle()), filter);
        }
        if (!savedAppointment.getDescription().equals(appointment.getDescription())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.DESCRIPTION, appointment.getDescription()), filter);
        }
        if (!savedAppointment.getLocation().equals(appointment.getLocation())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.LOCATION, appointment.getLocation()), filter);
        }
        if (!savedAppointment.getContact().equals(appointment.getContact())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.CONTACT, appointment.getContact()), filter);
        }
        if (!savedAppointment.getType().equals(appointment.getType())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.TYPE, appointment.getType()), filter);
        }
        if (!savedAppointment.getUrl().equals(appointment.getUrl())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.URL, appointment.getUrl()), filter);
        }
        if (!savedAppointment.getStart().equals(appointment.getStart())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.START, appointment.getStart()), filter);
        }
        if (!savedAppointment.getEnd().equals(appointment.getEnd())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.END, appointment.getEnd()), filter);
        }
        if (!savedAppointment.getLastUpdateBy().equals(appointment.getLastUpdateBy())) {
          QueryUtility.executeUpdateQuery(table, new Pair<>(Columns.LAST_UPDATE_BY, appointment.getLastUpdateBy()), filter);
        }

        currentAppointmentUpdated = true;
        new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
        AlertUtility.displayAlert(Alert.AlertType.INFORMATION, null, "Appointment Modified", "Appointment information was modified successfully.");

      } else if (appointment.getCurrentRecordResults().next()) {
        AlertUtility.displayErrorAlert("Appointment Already Exists", "The appointment already exists in the database.");

      } else {
        Integer appointmentResults = appointment.executeInsert();
        if (appointmentResults != 0) {
          currentAppointmentUpdated = true;
          new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
          AlertUtility.displayAlert(Alert.AlertType.INFORMATION, null, "New Appointment Added", "New appointment was added successfully.");
        }
      }

      if (currentAppointmentUpdated) {
        savedAppointment = appointment;
      }

    } else {
      AlertUtility.displayErrorAlert("Required Fields", "Please ensure that all required fields are filled before submitting.");
    }
  }

  public void handleCancelButton(ActionEvent event) {
    new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
  }

}
