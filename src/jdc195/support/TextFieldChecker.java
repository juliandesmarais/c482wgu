package jdc195.support;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class TextFieldChecker {

  public static boolean validateTextFieldContents(List<TextField> textFieldList) {
    List<String> failed = new ArrayList<>();
    for (TextField textField : textFieldList) {
      String text = textField.getText();
      if (text == null || text.isEmpty()) {
        failed.add(textField.getId());
      }
    }

    return failed.size() == 0;
  }

}
