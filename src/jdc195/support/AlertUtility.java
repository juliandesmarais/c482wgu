package jdc195.support;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import jdc195.database.ConnectionManager;

import java.sql.SQLException;
import java.util.Optional;

public class AlertUtility {

    public static boolean displayConfirmationAlert(String header, String content) {
        Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION, null, header, content);
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void displayExitConfirmationAlert() {
        if (displayConfirmationAlert("Exit", "Are you sure you want to exit?")) {
            try {
                ConnectionManager.getInstance().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }

    public static void displayErrorAlert(String title, String message) {
        displayAlert(AlertType.ERROR, null, title, message);
    }

    public static Optional<ButtonType> displayAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setWidth(250);

        if (title != null) {
            alert.setTitle(title);
        }
        if (header != null) {
            alert.setHeaderText(header);
        }
        if (content != null) {
            content = content.replace("[", "").replace("]", "").replace(",", "\n");
            alert.setContentText(content);
        }

        return  alert.showAndWait();
    }
}
