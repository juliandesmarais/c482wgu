package jdc482.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jdc482.model.Part;
import jdc482.model.Product;
import jdc482.support.AlertUtility;
import jdc482.support.GetFieldValueUtility;
import jdc482.support.InventoryManager;
import jdc482.support.LaunchViewUtility;
import jdc482.support.LaunchViewUtility.InventoryManagerView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
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
    private ObservableList<Part> currentAvailableParts;
    private ObservableList<Part> currentAssociatedParts;

    private Product getProductDetails() {
        invalidFields = new ArrayList<>();

        Integer idValue = getId();
        String nameValue = getName();
        Double priceValue = getPrice();
        Integer invValue = getInv();
        Integer minValue = getMin();
        Integer maxValue = getMax();

        if (idValue == null) {
            idValue = InventoryManager.getInstance().getLastProductId();
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

        if (currentAssociatedParts == null || currentAssociatedParts.isEmpty()) {
            invalidFields.add("At least one part must be associated with the product.");
        }

        if (!invalidFields.isEmpty()) {
            return null;
        }

        Product productToSave = new Product(idValue, nameValue, priceValue, invValue, minValue, maxValue);
        productToSave.setAssociatedParts(currentAssociatedParts);
        return productToSave;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idField.setDisable(true);
        setAvailablePartsTable();
        setAssociatedPartsTable();
    }

    private void setAvailablePartsTable() {
        availablePartsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        availablePartsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        availablePartsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        availablePartsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        currentAvailableParts = FXCollections.observableArrayList();
        currentAvailableParts.setAll(InventoryManager.getInstance().getAllParts());
        availablePartsTable.setItems(currentAvailableParts);
    }

    private void setAssociatedPartsTable() {
        associatedPartsTablePartIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        associatedPartsTableNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        associatedPartsTableInvLevelCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getStock()).asObject());
        associatedPartsTablePriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());

        currentAssociatedParts = FXCollections.observableArrayList();
        associatedPartsTable.setItems(currentAssociatedParts);
    }

    public void setProduct(Product product) {
        if (product != null) {
            titleLabel.setText("Modify Product");
            idField.setText(String.valueOf(product.getId()));
            nameField.setText(product.getName());
            invField.setText(String.valueOf(product.getStock()));
            priceCostField.setText(String.valueOf(product.getPrice()));
            maxField.setText(String.valueOf(product.getMax()));
            minField.setText(String.valueOf(product.getMin()));

            if (product.getAssociatedParts() != null && !product.getAssociatedParts().isEmpty()) {
                currentAssociatedParts.setAll(product.getAssociatedParts());
                currentAvailableParts.setAll(InventoryManager.getInstance().getAllParts().filtered(part -> !currentAssociatedParts.contains(part)));
            }
        }
    }

    private void refreshTables() {
        associatedPartsTable.refresh();
        availablePartsTable.refresh();
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

    @FXML
    private void handleAvailablePartsSearch() {
        String searchTerm = availablePartsSearchField.getText();
        availablePartsTable.setItems(currentAvailableParts.filtered(part -> part.getName().contains(searchTerm)));
    }

    @FXML
    private void clearAvailablePartSearchField() {
        availablePartsSearchField.clear();
        availablePartsTable.setItems(currentAvailableParts);
        refreshTables();
    }

    @FXML
    private void handleAddPartToProduct() {
        Part selectedPart = availablePartsTable.getSelectionModel().getSelectedItem();
        if (selectedPart != null) {
            currentAvailableParts.remove(selectedPart);
            currentAssociatedParts.add(selectedPart);
            refreshTables();
        }
    }

    @FXML
    private void handleDeletePartFromProduct() {
        Part selectedPart = associatedPartsTable.getSelectionModel().getSelectedItem();
        if (selectedPart != null) {
            Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                    null,
                    "Remove Associated Part",
                    "Are you sure you want to remove the associated part from the product?");

            if (result.get() == ButtonType.OK) {
                currentAssociatedParts.remove(selectedPart);
                currentAvailableParts.add(selectedPart);
                refreshTables();
            }
        }
    }

    private void displayFieldErrorAlert() {
        AlertUtility.displayAlert(Alert.AlertType.ERROR,
                "Error", "One or more field(s) were not filled properly:",
                invalidFields.toString());
        return;
    }

    @FXML
    private void handleSave(ActionEvent event) throws IOException {
        Product productToSave = getProductDetails();
        if (productToSave != null) {
            Product productInInventory = InventoryManager.getInstance().lookupProduct(productToSave.getId());
            if (productInInventory == null) { // Add Product
                InventoryManager.getInstance().addProduct(productToSave);
            } else { // Modify Product
                Integer index = InventoryManager.getInstance().getAllProducts().indexOf(productInInventory);
                InventoryManager.getInstance().updateProduct(index, productToSave);
            }

            new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.MAIN, event);
        } else {
            displayFieldErrorAlert();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                null,
                "Cancel",
                "Are you sure you want to cancel? All progress will be lost.");

        if (result.get() == ButtonType.OK) {
            new LaunchViewUtility().launchView(InventoryManagerView.MAIN, event);
        }
    }
}
