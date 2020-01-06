package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Part;
import model.Product;
import support.AlertUtility;
import support.GetFieldValueUtility;
import support.InventoryManager;
import support.LaunchViewUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProductScreenController implements Initializable {

    @FXML
    Label titleLabel;
    // Fields
    @FXML
    TextField idField;
    @FXML
    TextField nameField;
    @FXML
    TextField invField;
    @FXML
    TextField priceCostField;
    @FXML
    TextField minField;
    @FXML
    TextField maxField;
    @FXML
    TextField availablePartsSearchField;
    @FXML
    TableView<Part> availablePartsTable;
    @FXML
    TableView<Part> associatedPartsTable;
    @FXML
    private TableColumn<Part, Integer> availablePartsTablePartIdCol;
    @FXML
    private TableColumn<Part, String> availablePartsTableNameCol;
    @FXML
    private TableColumn<Part, Integer> availablePartsTableInvLevelCol;
    @FXML
    private TableColumn<Part, Double> availablePartsTablePriceCol;
    @FXML
    private TableColumn<Part, Integer> associatedPartsTablePartIdCol;
    @FXML
    private TableColumn<Part, String> associatedPartsTableNameCol;
    @FXML
    private TableColumn<Part, Integer> associatedPartsTableInvLevelCol;
    @FXML
    private TableColumn<Part, Double> associatedPartsTablePriceCol;

    private ArrayList<String> invalidFields;
    private Product currentProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idField.setDisable(true);
        setAvailablePartsTableColumns();
        setAssociatedPartsTableColumns();

        updateTables();
    }

    public void setProduct(Product product) {
        currentProduct = product;

        titleLabel.setText("Modify Product");
        idField.setText(String.valueOf(product.getId()));
        nameField.setText(product.getName());
        invField.setText(String.valueOf(product.getStock()));
        priceCostField.setText(String.valueOf(product.getPrice()));
        maxField.setText(String.valueOf(product.getMax()));
        minField.setText(String.valueOf(product.getMin()));

        updateTables();
    }

    private void setAvailablePartsTableColumns() {
        availablePartsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        availablePartsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        availablePartsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        availablePartsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    }

    private void setAssociatedPartsTableColumns() {
        associatedPartsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        associatedPartsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        associatedPartsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        associatedPartsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
    }

    private void updateTables() {
        if (currentProduct == null) {
            availablePartsTable.setItems(InventoryManager.shared().getAllParts());
        } else {
            ObservableList<Part> associatedParts = currentProduct.getAllAssociatedParts();
            ObservableList<Part> remainingAvailableParts = InventoryManager.shared().getAllParts().filtered(part -> !associatedParts.contains(part));
            availablePartsTable.setItems(remainingAvailableParts);
            associatedPartsTable.setItems(associatedParts);
        }
    }

    private Integer getId() {
        return GetFieldValueUtility.getIntValue(idField);
    }

    private String getName() {
        return GetFieldValueUtility.getStringValue(nameField);
    }

    private Double getPrice() {
        return GetFieldValueUtility.getDoubleValue(priceCostField);
    }

    private Integer getInv() {
        return GetFieldValueUtility.getIntValue(invField);
    }

    private Integer getMin() {
        return GetFieldValueUtility.getIntValue(minField);
    }

    private Integer getMax() {
        return GetFieldValueUtility.getIntValue(maxField);
    }

    private ObservableList<Part> getAssociatedParts() {
        return associatedPartsTable.getSelectionModel().getSelectedItems();
    }

    private Product getProductDetails() {
        invalidFields = new ArrayList<>();
        Integer idValue = getId();
        String nameValue = getName();
        Double priceValue = getPrice();
        Integer invValue = getInv();
        Integer minValue = getMin();
        Integer maxValue = getMax();
        ObservableList<Part> associatedParts = getAssociatedParts();

        if (idValue == null) {
            idValue = InventoryManager.shared().getLastProductId();
        }

        if (nameValue == null) {
            invalidFields.add("Name was not properly filled.");
        }

        if (priceValue == null) {
            invalidFields.add("Price was not properly filled (only digits/decimals are allowed).");
        }

        if (invValue == null) {
            invalidFields.add("Stock/inventory was not properly filled (only digits are allowed).");
        }

        if (minValue == null) {
            invalidFields.add("Minimum was not properly filled (only digits are allowed).");
        }

        if (maxValue == null) {
            invalidFields.add("Maximum was not properly filled (only digits are allowed).");
        }

        if (associatedParts.isEmpty()) {
            invalidFields.add("Product must be associated with at least one part.");
        }

        if (!invalidFields.isEmpty()) {
            return null;
        }

        return new Product(idValue, nameValue, priceValue, invValue, minValue, maxValue);
    }

    @FXML
    private void handleAvailablePartsSearch(ActionEvent event) throws IOException {
        String searchTerm = availablePartsSearchField.getText();
        availablePartsTable.setItems(InventoryManager.shared().lookupPart(searchTerm).filtered(part -> !getAssociatedParts().contains(part)));
    }

    @FXML
    private void clearAvailablePartSearchField(ActionEvent event) throws IOException {
        
    }

    @FXML
    private void handleAddPartToProduct() {
        if (currentProduct == null) {
            currentProduct = new Product();
        }

        for (Part selectedPart : availablePartsTable.getSelectionModel().getSelectedItems()) {
            currentProduct.addAssociatedPart(selectedPart);
        }

        updateTables();
    }

    @FXML
    private void handleDeletePartFromProduct() {
        currentProduct.deleteAssociatedPart(associatedPartsTable.getSelectionModel().getSelectedItem());
        updateTables();
    }

    @FXML
    private void handleSave(ActionEvent event) throws IOException {
        currentProduct = getProductDetails();

        if (currentProduct != null) {
            Part productInInventory = InventoryManager.shared().lookupPart(currentProduct.getId());

            if (productInInventory == null) { // Add Product
                InventoryManager.shared().addProduct(currentProduct);
            } else { // Edit Product
                Integer index = InventoryManager.shared().getAllProducts().indexOf(productInInventory);
                InventoryManager.shared().updateProduct(index, currentProduct);
            }

            new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.MAIN, event);
        } else {
            AlertUtility.displayAlert(Alert.AlertType.ERROR,
                    "Error", "One or more field(s) were not filled properly:",
                    invalidFields.toString());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.MAIN, event);
    }
}
