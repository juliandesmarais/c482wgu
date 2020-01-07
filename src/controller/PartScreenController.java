package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.InHouse;
import model.Outsourced;
import model.Part;
import support.AlertUtility;
import support.GetFieldValueUtility;
import support.InventoryManager;
import support.LaunchViewUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PartScreenController implements Initializable {

    @FXML
    RadioButton inHouseRadioButton;
    @FXML
    RadioButton outsourcedRadioButton;
    @FXML
    Label machineIdOrCompanyNameLabel;

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
    TextField machineIdOrCompanyNameField;
    @FXML
    TextField minField;
    @FXML
    TextField maxField;

    private List<String> invalidFields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idField.setDisable(true);

        ToggleGroup buttonGroup = new ToggleGroup();
        inHouseRadioButton.setToggleGroup(buttonGroup);
        outsourcedRadioButton.setToggleGroup(buttonGroup);
    }

    public void setPart(Part part) {
        titleLabel.setText("Modify Part");
        idField.setText(String.valueOf(part.getId()));
        nameField.setText(part.getName());
        invField.setText(String.valueOf(part.getStock()));
        priceCostField.setText(String.valueOf(part.getPrice()));
        maxField.setText(String.valueOf(part.getMax()));
        minField.setText(String.valueOf(part.getMin()));

        if (part instanceof Outsourced) {
            setOutsourcedState();
            machineIdOrCompanyNameField.setText(((Outsourced) part).getCompanyName());
        } else {
            setInHouseState();
            machineIdOrCompanyNameField.setText(String.valueOf(((InHouse) part).getMachineId()));
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

    private Integer getMachineId() {
        return GetFieldValueUtility.getIntValue(machineIdOrCompanyNameField);
    }

    private String getCompanyName() {
        return GetFieldValueUtility.getStringValue(machineIdOrCompanyNameField);
    }

    private Part getDisplayedPartDetails() {
        invalidFields = new ArrayList<>();
        Integer idValue = getId();
        String nameValue = getName();
        Double priceValue = getPrice();
        Integer invValue = getInv();
        Integer minValue = getMin();
        Integer maxValue = getMax();

        if (idValue == null) {
            idValue = InventoryManager.shared().getLastPartId();
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

        if (inHouseRadioButton.isSelected()) {
            Integer machineIdValue = getMachineId();
            if (machineIdValue == null) {
                invalidFields.add("Machine ID was not properly filled (only digits are allowed).");
            }

            if (!invalidFields.isEmpty()) {
                return null;
            }

            InHouse inHousePart = new InHouse(idValue, nameValue, priceValue, invValue, minValue, maxValue, machineIdValue);
            return inHousePart;
        } else if (outsourcedRadioButton.isSelected()) {
            String companyNameValue = getCompanyName();

            if (companyNameValue == null) {
                invalidFields.add("Company name was not properly filled.");
            }

            if (!invalidFields.isEmpty()) {
                return null;
            }

            Outsourced outsourcedPart = new Outsourced(idValue, nameValue, priceValue, invValue, minValue, maxValue, companyNameValue);
            return outsourcedPart;
        }

        return null;
    }

    private void setInHouseState() {
        inHouseRadioButton.setSelected(true);
        machineIdOrCompanyNameLabel.setText("Machine ID");
        machineIdOrCompanyNameField.setPromptText("Machine ID");
    }

    private void setOutsourcedState() {
        outsourcedRadioButton.setSelected(true);
        machineIdOrCompanyNameLabel.setText("Company Name");
        machineIdOrCompanyNameField.setPromptText("Company Name");
    }

    @FXML
    private void setInHouse() {
        setInHouseState();
    }

    @FXML
    private void setOutsourced() {
        setOutsourcedState();
    }

    @FXML
    private void handleSave(ActionEvent event) throws IOException {
        Part displayedPart = getDisplayedPartDetails();

        if (!invalidFields.isEmpty()) {
            AlertUtility.displayAlert(Alert.AlertType.ERROR,
                    "Error", "One or more field(s) were not filled properly:",
                    invalidFields.toString());
            return;
        }

        if (displayedPart != null) {
            Part partInInventory = InventoryManager.shared().lookupPart(displayedPart.getId());

            if (partInInventory == null) { // Add Part
                InventoryManager.shared().addPart(displayedPart);
            } else { // Edit Part
                Integer index = InventoryManager.shared().getAllParts().indexOf(partInInventory);
                InventoryManager.shared().updatePart(index, displayedPart);
            }

            new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.MAIN, event);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) throws IOException {
        Optional<ButtonType> result = AlertUtility.displayAlert(Alert.AlertType.CONFIRMATION,
                null,
                "Cancel",
                "Are you sure you want to cancel? All progress will be lost.");

        if (result.get() == ButtonType.OK) {
            new LaunchViewUtility().launchView(LaunchViewUtility.InventoryManagerView.MAIN, event);
        }
    }
}
