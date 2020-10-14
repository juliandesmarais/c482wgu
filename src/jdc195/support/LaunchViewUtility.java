package jdc195.support;

import jdc195.controller.AppointmentViewController;
import jdc195.controller.CustomerViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdc195.model.Appointment;
import jdc195.model.Customer;

import java.io.IOException;

public class LaunchViewUtility {

  public void launchModifyCustomerView(ActionEvent event, Customer customer) throws IOException {
    FXMLLoader loader = getLoaderWithPath("/view/Customer.fxml");
    Parent parentNode = loader.load();
    CustomerViewController controller = loader.getController();
    controller.setCustomer(customer);
    showSceneWithParentNode(event, parentNode);
  }

  public void launchModifyAppointmentView(ActionEvent event, Appointment appointment) throws IOException {
    FXMLLoader loader = getLoaderWithPath("/view/Appointment.fxml");
    Parent parentNode = loader.load();
    AppointmentViewController controller = loader.getController();
    controller.setAppointment(appointment);
    showSceneWithParentNode(event, parentNode);
  }

  public void launchView(ActionEvent event, View view) {
    String resourcePath = "/view/";

    switch (view) {
    case LOGIN:
      resourcePath += "Login.fxml";
      break;
    case OVERVIEW:
      resourcePath += "Overview.fxml";
      break;
    case ADD_CUSTOMER:
      resourcePath += "Customer.fxml";
      break;
    case ADD_APPT:
      resourcePath += "Appointment.fxml";
      break;
    }

    try {
      Parent parentNode = getLoaderWithPath(resourcePath).load();
      showSceneWithParentNode(event, parentNode);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void showSceneWithParentNode(ActionEvent sourceEvent, Parent parent) {
    Scene scene = new Scene(parent, Constants.MAIN_SCREEN_WIDTH, Constants.MAIN_SCREEN_HEIGHT);
    Stage stage = (Stage) ((Node) sourceEvent.getSource()).getScene().getWindow();
    stage.setScene(scene);
    stage.show();
  }

  private FXMLLoader getLoaderWithPath(String resourcePath) {
    return new FXMLLoader(getClass().getResource(resourcePath));
  }

  public enum View {
    LOGIN, OVERVIEW, ADD_CUSTOMER, ADD_APPT
  }
}
