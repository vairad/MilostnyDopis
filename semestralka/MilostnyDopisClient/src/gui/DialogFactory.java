package gui;

import javafx.scene.control.Alert;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class DialogFactory {


    /**
     * Create and show alert dialog with defined parameters
     * @param title window header
     * @param headline error headline
     * @param text long description
     */
    public static void alertError(String title, String headline, String text ){
        Alert alert  = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headline);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
