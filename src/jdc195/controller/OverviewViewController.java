package jdc195.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;
import jdc195.model.Appointment;
import jdc195.model.Appointment.AppointmentsTableDataFilter;
import jdc195.model.Customer;
import jdc195.support.*;
import jdc195.support.LaunchViewUtility.View;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class OverviewViewController implements Initializable {

  @FXML private Label appointmentsTableTitleLabel;
  @FXML private Button appointmentCalendarButton;
  @FXML private TableView<Customer> customersTableView;
  @FXML private TableColumn<Customer, Integer> customerIdCol;
  @FXML private TableColumn<Customer, String> customerNameCol;
  @FXML private TableColumn<Customer, Boolean> customerActiveCol;

  @FXML private TableView<Appointment> appointmentsTableView;
  @FXML private TableColumn<Appointment, String> appointmentsCustomerCol;
  @FXML private TableColumn<Appointment, String> appointmentsTitleCol;
  @FXML private TableColumn<Appointment, String> appointmentsDescriptionCol;
  @FXML private TableColumn<Appointment, String> appointmentsLocationCol;
  @FXML private TableColumn<Appointment, String> appointmentsContactCol;
  @FXML private TableColumn<Appointment, String> appointmentsTypeCol;
  @FXML private TableColumn<Appointment, String> appointmentsUrlCol;
  @FXML private TableColumn<Appointment, String> appointmentsStartDateCol;
  @FXML private TableColumn<Appointment, String> appointmentsEndDateCol;
  @FXML private TableColumn<Appointment, String> appointmentsCreatedDateCol;
  @FXML private TableColumn<Appointment, String> appointmentsCreatedByCol;
  @FXML private TableColumn<Appointment, String> appointmentsLastUpdateDateCol;
  @FXML private TableColumn<Appointment, String> appointmentsLastUpdateByCol;

  private static AppointmentsTableDataFilter appointmentsTableDataFilter = AppointmentsTableDataFilter.ALL;
  private static List<Appointment> appointmentsData;

  @Override public void initialize(URL location, ResourceBundle resources) {
    appointmentsData = null;

    setupCustomerTable();
    refreshCustomerTableView();

    setAppointmentCalendarElementLabels();
    setupAppointmentsTable();
    refreshAppointmentsTableView();
  }

  private void setAppointmentCalendarElementLabels() {
    final String appointmentCalendarDefaultText = "Appointment Calendar";
    final String appointmentCalendarFilterEnabledText = " √ Appointment Calendar";

    switch (appointmentsTableDataFilter) {
    case ALL:
      appointmentCalendarButton.setText(appointmentCalendarDefaultText);
      break;
    case CURRENT_MONTH:
      appointmentsTableTitleLabel.setText("Appointments (Current Month)");
      appointmentCalendarButton.setText(appointmentCalendarFilterEnabledText);
      break;
    case CURRENT_WEEK:
      appointmentsTableTitleLabel.setText("Appointments (Current Week)");
      appointmentCalendarButton.setText(appointmentCalendarFilterEnabledText);
      break;
    }
  }

  private Customer getSelectedCustomer() {
    return customersTableView.getSelectionModel().getSelectedItem();
  }

  private Appointment getSelectedAppointment() {
    return appointmentsTableView.getSelectionModel().getSelectedItem();
  }

  //region Action Handlers
  public void handleLogOutAction(ActionEvent event) {
    if (AlertUtility.displayConfirmationAlert("Log Out", "Are you sure you want to log out?")) {
      UserManager.getInstance().resetUser();
      new LaunchViewUtility().launchView(event, View.LOGIN);
    }
  }

  public void handleGenerateReportsAction(ActionEvent event) {
    final String appointmentTypesReport = "Appointment Types This Month";
    final String userAppointmentsReport = "Appointments for Current Consultant";
    final String customersWithAppointmentsReport = "Customers with Appointments This Month";
    final String[] allButtons = new String[] { appointmentTypesReport, userAppointmentsReport, customersWithAppointmentsReport };

    String resultText = DialogUtility.displayDialog(event,
        "Generate Report",
        "Please select which report type you would like to generate.\n\n",
        allButtons,
        true);

    if (appointmentsData == null) {
      try {
        appointmentsData = Appointment.getAppointmentsTableData(AppointmentsTableDataFilter.ALL);
      } catch (SQLException e) {
        AlertUtility.displayErrorAlert("Error", "An error occurred when retrieving appointment data.");
        e.printStackTrace();
        return;
      }
    }

    if (resultText.contains(appointmentTypesReport)) {
      showAppointmentTypesReport(event);
    } else if (resultText.contains(userAppointmentsReport)) {
      showUserAppointmentsReport(event);
    } else if (resultText.contains(customersWithAppointmentsReport)) {
      showCustomersWithAppointmentsReport(event);
    }
  }

  private void showAppointmentTypesReport(ActionEvent sourceEvent) {
    System.out.println("Displaying report: Appointment types this month.");
    String headerText = String.format("Appointment Types This Month (%s)", DateUtility.getCurrentMonth());

    Map<String, Long> map = appointmentsData.stream().collect(groupingBy(Appointment::getType, counting()));

    StringBuilder actualMap = new StringBuilder();
    map.forEach((k, v) -> {
      actualMap.append("Type: ").append(k);
      actualMap.append("  |  Count: ").append(v).append("\n");
    });

    if (DialogUtility.displayBasicDialog(sourceEvent, headerText, actualMap.toString())) {
      handleGenerateReportsAction(sourceEvent);
    }
  }

  private void showUserAppointmentsReport(ActionEvent sourceEvent) {
    System.out.println("Displaying report: Appointments for current consultant (user).");
    String headerText = "Appointments for Current Consultant";

    Map<Integer, Long> map = appointmentsData.stream().collect(groupingBy(Appointment::getUserId, counting()));

    StringBuilder actualMap = new StringBuilder();
    map.forEach((k, v) -> {
      if (k.equals(UserManager.getInstance().getUser().getUserId())) {
        actualMap.append("Current Consultant Info: \n").append(UserManager.getInstance().getUser().toString()).append("\n");
        actualMap.append("Appointment Count: ").append(v).append("\n");
      }
    });

    if (DialogUtility.displayBasicDialog(sourceEvent, headerText, actualMap.toString())) {
      handleGenerateReportsAction(sourceEvent);
    }
  }

  private void showCustomersWithAppointmentsReport(ActionEvent sourceEvent) {
    System.out.println("Displaying report: Customers with appointments this month.");
    String headerText = String.format("Customers With Appointments This Month (%s)", DateUtility.getCurrentMonth());

    StringBuilder actualMap = new StringBuilder();
    Map<Integer, Long> map = appointmentsData.stream().collect(groupingBy(Appointment::getCustomerId, counting()));
    map.forEach((k, v) -> {
      try {
        actualMap.append("Customer Name: ").append(Customer.getCustomerNameWithId(k));
        actualMap.append("  |  Appointments: ").append(v);
      } catch (SQLException e) {
        AlertUtility.displayErrorAlert("Error", "An error occurred when retrieving customer data.");
        e.printStackTrace();
      }
    });

    if (DialogUtility.displayBasicDialog(sourceEvent, headerText, actualMap.toString())) {
      handleGenerateReportsAction(sourceEvent);
    }
  }

  public void handleAddCustomerAction(ActionEvent event) {
    new LaunchViewUtility().launchView(event, View.ADD_CUSTOMER);
  }

  public void handleModifyCustomerAction(ActionEvent event) throws IOException {
    new LaunchViewUtility().launchModifyCustomerView(event, getSelectedCustomer());
  }

  public void handleDeleteCustomerAction() {
    if (AlertUtility.displayConfirmationAlert("Delete Customer", "Are you sure you wanted to delete the selected customer?")) {
      try {
        QueryUtility.executeDelete(Tables.CUSTOMER, new Pair<>(Columns.CUSTOMER_ID, getSelectedCustomer().getCustomerId()));
        refreshCustomerTableView();
      } catch (SQLException e) {
        AlertUtility.displayErrorAlert("Error", "The selected customer is tied to one or more appointments and cannot currently be deleted.\n\nDelete the associated appointments first.");
        e.printStackTrace();
      }
    }
  }

  public void handleAddAppointmentAction(ActionEvent event) {
    new LaunchViewUtility().launchView(event, View.ADD_APPOINTMENT);
  }

  public void handleModifyAppointmentAction(ActionEvent event) throws IOException {
    new LaunchViewUtility().launchModifyAppointmentView(event, getSelectedAppointment());
  }

  public void handleDeleteAppointmentAction() {
    if (AlertUtility.displayConfirmationAlert("Delete Appointment", "Are you sure you wanted to delete the selected appointment?")) {
      try {
        QueryUtility.executeDelete(Tables.APPOINTMENT, new Pair<>(Columns.APPOINTMENT_ID, getSelectedAppointment().getAppointmentId()));
        refreshAppointmentsTableView();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void handleExitAction() {
    AlertUtility.displayExitConfirmationAlert();
  }

  public void handleAppointmentCalendarAction(ActionEvent event) {
    final String currentWeekOption = "Current Week";
    final String currentMonthOption = "Current Month";
    final String allOption = "All";
    final String[] allButtons = new String[] { currentWeekOption, currentMonthOption, allOption };

    String resultText = DialogUtility.displayDialog(event,
        "Filter Appointments",
        "Please select which appointments you would like to see on the appointments list.\n\n",
        allButtons,
        true);

    if (resultText.contains(currentWeekOption)) {
      appointmentsTableDataFilter = AppointmentsTableDataFilter.CURRENT_WEEK;
      System.out.println("Displaying appointments by current week.");
    } else if (resultText.contains(currentMonthOption)) {
      appointmentsTableDataFilter = AppointmentsTableDataFilter.CURRENT_MONTH;
      System.out.println("Displaying appointments by current month.");
    } else if (resultText.contains(allOption)) {
      appointmentsTableDataFilter = AppointmentsTableDataFilter.ALL;
      System.out.println("Displaying all appointments.");
    }

    setAppointmentCalendarElementLabels();

    if (!resultText.contains("Cancel")) {
      refreshAppointmentsTableView();
    }
  }
  //endregion Action Handlers

  //region Appointments Table
  private void setupAppointmentsTable() {

    appointmentsCustomerCol.setCellValueFactory(c -> {
      String customerName = "";
      try {
        customerName = Customer.getCustomerNameWithId(c.getValue().getCustomerId());
      } catch (SQLException e) {
        e.printStackTrace();
      }

      return new SimpleStringProperty(customerName);
    });

    appointmentsTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
    appointmentsDescriptionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
    appointmentsLocationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));
    appointmentsContactCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContact()));
    appointmentsTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
    appointmentsUrlCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUrl()));
    appointmentsStartDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStart().toLocalDateTime().toString()));
    appointmentsEndDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnd().toLocalDateTime().toString()));
    appointmentsCreatedDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreateDate().toLocalDateTime().toString()));
    appointmentsLastUpdateDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastUpdate().toLocalDateTime().toString()));
    appointmentsCreatedByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedBy()));
    appointmentsLastUpdateByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastUpdateBy()));
  }

  private void refreshAppointmentsTableView() {
    try {
      appointmentsTableView.setItems(Appointment.getAppointmentsTableData(appointmentsTableDataFilter));
    } catch (SQLException e) {
      e.printStackTrace();

    }
  }
  //endregion Appointments Table

  //region Customer Table
  private void setupCustomerTable() {
    customerIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
    customerNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));
    customerActiveCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getActive()).asObject());
  }

  private void refreshCustomerTableView() {
    try {
      customersTableView.getItems().clear();
      customersTableView.setItems(Customer.getCustomersTableData());
      customersTableView.setPrefWidth(customerIdCol.getPrefWidth() + customerNameCol.getPrefWidth() + customerActiveCol.getPrefWidth());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  //endregion Customer Table


}
