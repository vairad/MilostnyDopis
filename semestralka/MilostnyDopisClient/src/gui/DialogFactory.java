package gui;

import game.Card;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public static boolean yesNoQuestion(String title, String question) {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    public static Card guardianChose() {
        List<Card> choices = new ArrayList<>();
        choices.add(Card.PRIEST);
        choices.add(Card.BARON);
        choices.add(Card.KOMORNA);
        choices.add(Card.PRINCE);
        choices.add(Card.KING);
        choices.add(Card.COUNTESS);
        choices.add(Card.PRINCESS);

        ChoiceDialog<Card> dialog = new ChoiceDialog<>(Card.PRIEST, choices);
        dialog.setTitle(App.bundle.getString("guardianChooseTitle"));
        dialog.setHeaderText(App.bundle.getString("guardianChooseQuestion"));
        dialog.setContentText(App.bundle.getString("guardianChooseText"));

        Optional<Card> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return null;
    }
}
