package support;

import controller.PartScreenController;
import controller.ProductScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Part;
import model.Product;

import java.io.IOException;

public class LaunchViewUtility {
    public void launchModifyPartView(ActionEvent event, Part part) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/PartScreen.fxml"));
        Parent parent = loader.load();

        PartScreenController controller = loader.getController();
        controller.setPart(part);

        Scene scene = new Scene(parent, Constants.PART_SCREEN_WIDTH, Constants.PART_SCREEN_HEIGHT);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void launchProductView(ActionEvent event, Product product) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/ProductScreen.fxml"));
        Parent parent = loader.load();

        ProductScreenController controller = loader.getController();
        controller.setProduct(product);

        Scene scene = new Scene(parent, Constants.PRODUCT_SCREEN_WIDTH, Constants.PRODUCT_SCREEN_HEIGHT);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void launchView(InventoryManagerView view, ActionEvent event) throws IOException {
        String resourcePath = "../view/";
        double sceneWidth = Constants.MAIN_SCREEN_WIDTH;
        double sceneHeight = Constants.MAIN_SCREEN_HEIGHT;

        switch (view) {
            case MAIN:
                resourcePath += "MainScreen.fxml";
                break;
            case ADD_PART:
                resourcePath += "PartScreen.fxml";
                sceneWidth = Constants.PART_SCREEN_WIDTH;
                sceneHeight = Constants.PART_SCREEN_HEIGHT;
                break;
        }

        Parent parent = FXMLLoader.load(getClass().getResource(resourcePath));
        Scene scene = new Scene(parent, sceneWidth, sceneHeight);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public enum InventoryManagerView {
        MAIN, ADD_PART
    }
}
