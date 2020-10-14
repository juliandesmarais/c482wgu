package jdc195.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jdc195.model.Appointment;
import jdc195.model.User;
import jdc195.support.AlertUtility;
import jdc195.support.LaunchViewUtility;
import jdc195.support.UserManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginViewController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private String errorAlertTitle = "Login Error";
    private String errorAlertMessage = "The username or password did not match.";

    @Override public void initialize(URL location, ResourceBundle resources) {
        setFieldLabelsForLocale(Locale.getDefault());
    }

    private void setFieldLabelsForLocale(Locale locale) {
        if (Locale.FRANCE.equals(locale)) {
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Mot de passe");
            loginButton.setText("Connexion");
            errorAlertTitle = "Erreur d'identification";
            errorAlertMessage = "Le nom d'utilisateur ou le mot de passe ne correspond pas.";
        } else if (Locale.JAPAN.equals(locale)) {
            usernameLabel.setText("ユーザー名");
            passwordLabel.setText("パスワード");
            loginButton.setText("ログイン");
            errorAlertTitle = "ログインエラー";
            errorAlertMessage = "ユーザー名またはパスワードが一致しませんでした。";
        }
    }

    public void handleLoginAction(ActionEvent actionEvent) {
        String usernameValue = usernameField.getText();
        String passwordValue = passwordField.getText();

        if (!usernameValue.isEmpty() && !passwordValue.isEmpty()) {
            User foundUser = User.getUserWithCredentials(usernameValue, passwordValue);
            if (foundUser == null) {
                AlertUtility.displayErrorAlert(errorAlertTitle, errorAlertMessage);
            } else {
                UserManager.getInstance().setUser(foundUser);
                new LaunchViewUtility().launchView(actionEvent, LaunchViewUtility.View.OVERVIEW);

                try {
                    Appointment.checkLoginTimeWithinAppointmentWindow();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleExitAction() {
        AlertUtility.displayExitConfirmationAlert();
    }
}
