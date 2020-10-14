package jdc195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Pair;
import jdc195.database.QueryConstants;
import jdc195.database.QueryConstants.Columns;
import jdc195.database.QueryUtility;
import jdc195.model.*;
import jdc195.support.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {

  @FXML private Label customerViewTitleLabel;
  @FXML private TextField customerNameField;
  @FXML private TextField addressLine1Field;
  @FXML private TextField addressLine2Field;
  @FXML private TextField cityField;
  @FXML private TextField postalCodeField;
  @FXML private TextField phoneNumberField;
  @FXML private RadioButton activeYesButton;
  @FXML private RadioButton activeNoButton;
  @FXML private ComboBox<String> countryField;

  private Customer savedCustomer = null;

  @Override public void initialize(URL location, ResourceBundle resources) {
    countryField.getSelectionModel().selectFirst();
  }

  public void setCustomer(Customer customer) {
    try {
      ResultSet existingAddress = Address.getResultsWithAddressId(customer.getAddressId());

      if (existingAddress.next()) {
        addressLine1Field.setText(existingAddress.getString(Columns.ADDRESS.getColumnName()));
        addressLine2Field.setText(existingAddress.getString(Columns.ADDRESS_2.getColumnName()));
        postalCodeField.setText(existingAddress.getString(Columns.POSTAL_CODE.getColumnName()));
        phoneNumberField.setText(existingAddress.getString(Columns.PHONE.getColumnName()));

        ResultSet existingCity = City.getResultsWithCityId(existingAddress.getInt(Columns.CITY_ID.getColumnName()));
        if (existingCity.next()) {
          cityField.setText(existingCity.getString(Columns.CITY.getColumnName()));

          ResultSet existingCountry = Country.getResultsWithCountryId(existingCity.getInt(Columns.COUNTRY_ID.getColumnName()));

          if (existingCountry.next()) {
            countryField.getSelectionModel().select(existingCountry.getString(Columns.COUNTRY.getColumnName()));
          }
        }

        savedCustomer = customer;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (customer.getActive()) {
      activeYesButton.setSelected(true);
    } else {
      activeNoButton.setSelected(true);
    }

    customerNameField.setText(customer.getCustomerName());
    customerViewTitleLabel.setText("Edit Customer");
  }

  private boolean isRequiredFieldsFilled() {
    List<TextField> textFields = new ArrayList<>();
    textFields.add(customerNameField);
    textFields.add(addressLine1Field);
    textFields.add(cityField);
    textFields.add(postalCodeField);
    textFields.add(phoneNumberField);
    return TextFieldChecker.validateTextFieldContents(textFields);
  }

  private Customer getCurrentCustomer() throws SQLException, ParseException {
    Boolean isCustomerActivated = activeYesButton.isSelected();

    return new Customer()
        .setCustomerName(customerNameField.getText())
        .setAddressId(getAddressKeyForEnteredFieldValue())
        .setActive(isCustomerActivated);
  }

  public void handleSaveButton(ActionEvent event) throws SQLException, ParseException {
    boolean currentCustomerUpdated = false;

    if (isRequiredFieldsFilled()) {
      Customer customer = getCurrentCustomer();

      if (savedCustomer != null && !savedCustomer.equals(customer)) {
        Pair<Columns, Object> filter = new Pair<>(Columns.CUSTOMER_ID, savedCustomer.getCustomerId());

        if (!savedCustomer.getCustomerName().equals(customer.getCustomerName())) {
          QueryUtility.executeUpdateQuery(QueryConstants.Tables.CUSTOMER, new Pair<>(Columns.CUSTOMER_NAME, customer.getCustomerName()), filter);
        }
        if (!savedCustomer.getAddressId().equals(customer.getAddressId())) {
          QueryUtility.executeUpdateQuery(QueryConstants.Tables.CUSTOMER, new Pair<>(Columns.ADDRESS_ID, customer.getAddressId()), filter);
        }
        if (!savedCustomer.getActive().equals(customer.getActive())) {
          QueryUtility.executeUpdateQuery(QueryConstants.Tables.CUSTOMER, new Pair<>(Columns.ACTIVE, customer.getActive()), filter);
        }
        if (!savedCustomer.getLastUpdate().equals(customer.getLastUpdate())) {
          QueryUtility.executeUpdateQuery(QueryConstants.Tables.CUSTOMER, new Pair<>(Columns.LAST_UPDATE, customer.getLastUpdate()), filter);
        }
        if (!savedCustomer.getLastUpdateBy().equals(customer.getLastUpdateBy())) {
          QueryUtility.executeUpdateQuery(QueryConstants.Tables.CUSTOMER, new Pair<>(Columns.LAST_UPDATE_BY, customer.getLastUpdateBy()), filter);
        }

        currentCustomerUpdated = true;
        new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
        AlertUtility.displayAlert(Alert.AlertType.INFORMATION, null, "Customer Modified", "Customer information was modified successfully.");

      } else if (customer.getCurrentRecordResults().next()) {
        AlertUtility.displayErrorAlert("Customer Already Exists", "The customer already exists in the database.");

      } else {
        Integer customerResults = customer.executeInsert();
        if (customerResults != 0) {
          currentCustomerUpdated = true;
          new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
          AlertUtility.displayAlert(Alert.AlertType.INFORMATION, null, "New Customer Added", "New customer was added successfully.");
        }
      }

      if (currentCustomerUpdated) {
        savedCustomer = customer;
      }

    } else {
      AlertUtility.displayErrorAlert("Required Fields", "Please ensure that all required fields are filled before submitting.");
    }
  }

  public void handleCancelButton(ActionEvent event) {
    new LaunchViewUtility().launchView(event, LaunchViewUtility.View.OVERVIEW);
  }

  private Integer getCityKeyForEnteredFieldValue() throws SQLException {
    Integer countryId = Country.getCountryIdWithName(countryField.getSelectionModel().getSelectedItem());

    City city = new City()
        .setCity(cityField.getText())
        .setCountryId(countryId);

    ResultSet cityResultSet = city.getCurrentRecordResults();
    if (cityResultSet.next()) {
      return cityResultSet.getInt(Columns.CITY_ID.getColumnName());
    } else {
      return city.executeInsert();
    }
  }

  private Integer getAddressKeyForEnteredFieldValue() throws SQLException, ParseException {
    Address address = new Address()
        .setAddress(addressLine1Field.getText())
        .setAddress2(addressLine2Field.getText())
        .setCityId(getCityKeyForEnteredFieldValue())
        .setPostalCode(postalCodeField.getText())
        .setPhone(phoneNumberField.getText());

    ResultSet addressResultSet = address.getCurrentRecordResults();
    if (addressResultSet.next()) {
      return addressResultSet.getInt(Columns.ADDRESS_ID.getColumnName());
    } else {
      return address.executeInsert();
    }
  }
}
