package jdc195.support;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.StageStyle;

import java.util.Optional;

public class DialogUtility {

  /**
   * Display a basic Dialog window with specified content node, a back button, and a close button. Returns true if the user clicks the back button.
   * @param sourceEvent The source action event.
   * @param contentNode The node to display as the content.
   * @return True if the user clicks the back button, false otherwise.
   */
  public static boolean displayBasicDialog(ActionEvent sourceEvent, Node contentNode) {
    Dialog<String> dialog = new Dialog<>();
    dialog.getDialogPane().setContent(contentNode);
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("Back", ButtonBar.ButtonData.BACK_PREVIOUS));
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
    dialog.initStyle(StageStyle.DECORATED);
    dialog.initOwner(((Node) sourceEvent.getSource()).getScene().getWindow());

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent() && !result.toString().isEmpty()) {
      return result.toString().contains("Back");
    } else {
      throw new IllegalStateException(String.format("Failed to retrieve dialog result text successfully. Dialog: %s", dialog.toString()));
    }
  }

  /**
   * Display a basic Dialog window with header text, context text, a back button, and a close button. Returns true if the user clicks the back button.
   * @param sourceEvent The source action event.
   * @param headerText The text to display as the header.
   * @param contentText The text to display as the content.
   * @return True if the user clicks the back button, false otherwise.
   */
  public static boolean displayBasicDialog(ActionEvent sourceEvent, String headerText, String contentText) {
    Dialog<String> dialog = new Dialog<>();
    dialog.getDialogPane().setHeaderText(headerText);
    dialog.getDialogPane().setContentText(contentText);
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("Back", ButtonBar.ButtonData.BACK_PREVIOUS));
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE));
    dialog.initStyle(StageStyle.DECORATED);
    dialog.initOwner(((Node) sourceEvent.getSource()).getScene().getWindow());

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent() && !result.toString().isEmpty()) {
      return result.toString().contains("Back");
    } else {
      throw new IllegalStateException(String.format("Failed to retrieve dialog result text successfully. Dialog: %s", dialog.toString()));
    }
  }

  /**
   * Display a dialog with the given parameters. Accepts an array of String that will be used to create the buttons in the dialog.
   * @param sourceEvent The source action event.
   * @param headerText The text to display as the header.
   * @param contentText The text to display as the content.
   * @param buttonTitles The titles of the buttons to add to the dialog.
   * @param withCancelButton Whether or not to add a Cancel button to the dialog.
   * @return The result text of the button that was clicked.
   */
  public static String displayDialog(ActionEvent sourceEvent, String headerText, String contentText, String[] buttonTitles, boolean withCancelButton) {
    Dialog<String> dialog = new Dialog<>();
    dialog.getDialogPane().setHeaderText(headerText);
    dialog.getDialogPane().setContentText(contentText);

    for (String buttonType : buttonTitles) {
      ButtonType currentButton = new ButtonType(buttonType);
      dialog.getDialogPane().getButtonTypes().add(currentButton);
    }

    if (withCancelButton) {
      dialog.getDialogPane().getButtonTypes().add(new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
    }

    dialog.initStyle(StageStyle.DECORATED);
    dialog.initOwner(((Node) sourceEvent.getSource()).getScene().getWindow());

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent() && !result.toString().isEmpty()) {
      return result.toString();
    } else {
      throw new IllegalStateException(String.format("Failed to retrieve dialog result text successfully. Dialog: %s", dialog.toString()));
    }
  }

}
