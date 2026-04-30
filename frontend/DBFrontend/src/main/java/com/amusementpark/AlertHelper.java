package com.amusementpark;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * AlertHelper: centralised dialog utility.
 * Keeps all controllers free of repetitive Alert boilerplate.
 */
public class AlertHelper {

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
            AlertHelper.class.getResource("/css/style.css").toExternalForm()
        );
        alert.showAndWait();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
            AlertHelper.class.getResource("/css/style.css").toExternalForm()
        );
        alert.showAndWait();
    }

    /**
     * Confirmation dialog. Returns true if user clicked OK.
     */
    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
            AlertHelper.class.getResource("/css/style.css").toExternalForm()
        );
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
