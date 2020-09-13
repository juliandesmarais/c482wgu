package jdc482.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jdc482.model.Part;
import jdc482.model.Product;
import jdc482.support.AlertUtility;
import jdc482.support.InventoryManager;
import jdc482.support.LaunchViewUtility;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {

    // Parts Elements
    @FXML
    private TextField partsSearchField;
    @FXML
    private TableView<Part> partsTable;
    @FXML
    private TableColumn<Part, Integer> partsTablePartIdCol;
    @FXML
    private TableColumn<Part, String> partsTableNameCol;
    @FXML
    private TableColumn<Part, Integer> partsTableInvLevelCol;
    @FXML
    private TableColumn<Part, Double> partsTablePriceCol;

    // Products Elements
    @FXML
    private TextField productsSearchField;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> productsTablePartIdCol;
    @FXML
    private TableColumn<Product, String> productsTableNameCol;
    @FXML
    private TableColumn<Product, Integer> productsTableInvLevelCol;
    @FXML
    private TableColumn<Product, Double> productsTablePriceCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setPartsTableColumns();
        updatePartsTable();
        setProductsTableColumns();
        updateProductsTable();
    }

    @FXML
    private void handlePartsSearch() {
        String searchTerm = partsSearchField.getText();
        partsTable.setItems(InventoryManager.shared().lookupPart(searchTerm));
    }

    @FXML
    private void clearPartSearchField() {
        partsSearchField.clear();
        updatePartsTable();
    }

    @FXML
    public void launchAddPartScreen(ActionEvent event) throws IOException {
        new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.ADD_PART, event);
    }

    @FXML
    private void launchModifyPartView(ActionEvent event) throws IOException {
        if (partsTable.getSelectionModel().getSelectedItem() != null) {
            new LaunchViewUtility().launchModifyPartView(event, getSelectedPartById());
        }
    }

    @FXML
    private void handlePartsDelete() {
        if (partsTable.getSelectionModel().getSelectedItem() != null) {
            Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                    null,
                    "Delete Part",
                    "Are you sure you want to delete the selected part?");
            if (result.get() == ButtonType.OK) {
                if (InventoryManager.shared().deletePart(getSelectedPartById())) {
                    updatePartsTable();
                }
            }
        }
    }

    @FXML
    private void handleProductsSearch() {
        String searchTerm = productsSearchField.getText();
        productsTable.setItems(InventoryManager.shared().lookupProduct(searchTerm));
    }

    @FXML
    private void clearProductSearchField() {
        updateProductsTable();
        productsSearchField.clear();
    }

    @FXML
    private void launchAddProductView(ActionEvent event) throws IOException {
        new LaunchViewUtility().launchProductView(event, null);
    }

    @FXML
    private void launchModifyProductView(ActionEvent event) throws IOException {
        if (productsTable.getSelectionModel().getSelectedItem() != null) {
            new LaunchViewUtility().launchProductView(event, getSelectedProductById());
        }
    }

    @FXML
    private void handleProductsDelete() {
        if (productsTable.getSelectionModel().getSelectedItem() != null) {
            Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                    null,
                    "Delete Product",
                    "Are you sure you want to delete the selected product?");
            if (result.get() == ButtonType.OK) {
                if (InventoryManager.shared().deleteProduct(getSelectedProductById())) {
                    updateProductsTable();
                }
            }
        }
    }

    @FXML
    private void handleExit() {
        Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                null,
                "Exit",
                "Are you sure you want to exit?");

        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void setPartsTableColumns() {
        partsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        partsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        partsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        partsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    }

    private void setProductsTableColumns() {
        productsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        productsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        productsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        productsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    }

    private void updatePartsTable() {
        partsTable.setItems(InventoryManager.shared().getAllParts());
    }

    private void updateProductsTable() {
        productsTable.setItems(InventoryManager.shared().getAllProducts());
    }

    private Part getSelectedPartById() {
        return InventoryManager.shared().lookupPart(partsTable.getSelectionModel().getSelectedItem().getId());
    }

    private Product getSelectedProductById() {
        return InventoryManager.shared().lookupProduct(productsTable.getSelectionModel().getSelectedItem().getId());
    }
}
