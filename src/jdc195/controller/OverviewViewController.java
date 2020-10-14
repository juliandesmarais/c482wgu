package jdc195.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryConstants.Tables;
import jdc195.database.QueryUtility;
import jdc195.model.Appointment;
import jdc195.model.Customer;
import jdc195.support.AlertUtility;
import jdc195.support.LaunchViewUtility;
import jdc195.support.LaunchViewUtility.View;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OverviewViewController implements Initializable {

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

  @Override public void initialize(URL location, ResourceBundle resources) {
    setupCustomerTable();
    setCustomerTableValues();

    setupAppointmentsTable();
    setAppointmentsTableValues();
  }

  private Customer getSelectedCustomer() {
    return customersTableView.getSelectionModel().getSelectedItem();
  }

  private Appointment getSelectedAppointment() {
    return appointmentsTableView.getSelectionModel().getSelectedItem();
  }

  //region Action Handlers
  public void handleGenerateReportsAction(ActionEvent event) {

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
        refreshCustomerTable();
      } catch (SQLException e) {
        AlertUtility.displayErrorAlert("Error", "The selected customer is tied to one or more appointments and cannot currently be deleted.\n\nDelete the associated appointments first.");
        e.printStackTrace();
      }
    }
  }

  public void handleAddAppointmentAction(ActionEvent event) {
    new LaunchViewUtility().launchView(event, View.ADD_APPT);
  }

  public void handleModifyAppointmentAction(ActionEvent event) throws IOException {
    new LaunchViewUtility().launchModifyAppointmentView(event, getSelectedAppointment());
  }

  public void handleDeleteAppointmentAction() {
    if (AlertUtility.displayConfirmationAlert("Delete Appointment", "Are you sure you wanted to delete the selected appointment?")) {
      try {
        QueryUtility.executeDelete(Tables.APPOINTMENT, new Pair<>(Columns.APPOINTMENT_ID, getSelectedAppointment().getAppointmentId()));
        refreshAppointmentsTable();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void handleExitAction() {
    AlertUtility.displayExitConfirmationAlert();
  }

  public void handleAppointmentCalendarAction(ActionEvent event) {

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
    appointmentsStartDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStart().toString()));
    appointmentsEndDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnd().toString()));
    appointmentsCreatedDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreateDate().toString()));
    appointmentsLastUpdateDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastUpdate().toString()));
    appointmentsCreatedByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedBy()));
    appointmentsLastUpdateByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastUpdateBy()));
  }

  private void setAppointmentsTableValues() {
    try {
      appointmentsTableView.setItems(Appointment.getAppointmentsTableData());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void refreshAppointmentsTable() {
    appointmentsTableView.getItems().clear();
    setAppointmentsTableValues();
  }
  //endregion Appointments Table

  //region Customer Table
  private void setupCustomerTable() {
    customerIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCustomerId()).asObject());
    customerNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomerName()));
    customerActiveCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().getActive()).asObject());
  }

  private void setCustomerTableValues() {
    try {
      customersTableView.setItems(Customer.getCustomersTableData());
      customersTableView.setPrefWidth(customerIdCol.getPrefWidth() + customerNameCol.getPrefWidth() + customerActiveCol.getPrefWidth());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void refreshCustomerTable() {
    customersTableView.getItems().clear();
    setCustomerTableValues();
  }
  //endregion Customer Table

}
