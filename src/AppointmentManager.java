import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdc195.database.ConnectionManager;
import jdc195.support.Constants;

public class AppointmentManager extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Connect to DB
        ConnectionManager.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Appointment Management System");
        stage.setScene(scene);
        stage.setMinHeight(Constants.MAIN_SCREEN_HEIGHT);
        stage.setMinWidth(Constants.MAIN_SCREEN_WIDTH);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}