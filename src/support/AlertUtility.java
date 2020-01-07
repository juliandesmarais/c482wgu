package support;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtility {

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
