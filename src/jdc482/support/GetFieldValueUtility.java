package jdc482.support;

import javafx.scene.control.TextField;

public class GetFieldValueUtility {

    public static Integer getIntValue(TextField field) {
        String fieldValue = field.getText();

        if (!fieldValue.isEmpty()) {
            try {
                return Integer.parseInt(fieldValue) ;
            } catch (Exception e) {
                System.out.print(e);
            }
        }

        return null;
    }

    public static Double getDoubleValue(TextField field) {
        String fieldValue = field.getText();

        if (!fieldValue.isEmpty()) {
            try {
                return Double.parseDouble(fieldValue) ;
            } catch (Exception e) {
                System.out.print(e);
            }
        }

        return null;
    }

    public static String getStringValue(TextField field) {
        String fieldValue = field.getText();

        if (!fieldValue.isEmpty()) {
            try {
                return fieldValue;
            } catch (Exception e) {
                System.out.print(e);
            }
        }

        return null;
    }
}
