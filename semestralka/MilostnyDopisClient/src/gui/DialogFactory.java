package gui;

import game.Card;
import game.Player;
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

    public static void returnedCard(Card card) {
        Alert alert  = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(App.bundle.getString("returnedCardTitle"));
        alert.setHeaderText(App.bundle.getString("returnedCardHeadline"));
        alert.setContentText(App.bundle.getString("returnedCardText") + " : " + card);
        alert.showAndWait();
    }

    public static void resultDialog(Card playedCard, Player playerWhoPlays, boolean isMyTarget, String cardResult) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Výsledek karty");
        if(!isMyTarget){
            alert.setHeaderText("Čelil jsi efektu karty s tímto výsledkem");
        } else{
            alert.setHeaderText("Záhrál jsi kartu s tímto výsledkem:");
        }
        alert.setContentText(playedCard.toString() + " " + playerWhoPlays.getDisplayName() + " " + cardResult);
        alert.showAndWait();
        //todo resources
        //todo resolvecard result
    }

    public static int gameCountDialog() {
        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(3);
        choices.add(5);
        choices.add(7);
        choices.add(9);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, choices);
        dialog.setTitle(App.bundle.getString("gameCountTitle"));
        dialog.setHeaderText(App.bundle.getString("gameCountQuestion"));
        dialog.setContentText(App.bundle.getString("gameCountText"));

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return -1;
    }

    public static void roundEndDialog(List<Player> winners, Card winCard){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.bundle.getString("roundWinnerTitle"));
        String headline = App.bundle.getString("roundWinnerHeadline") + " " + winCard;
        alert.setHeaderText(headline);
        String text = App.bundle.getString("roundWinnerText");
        text += "\n";
        for (Player p: winners) {
            text += p.getDisplayName() + "\n";
        }
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static void gameStandingsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Konec hry"); //todo resources
        alert.showAndWait();
    }

    public static void wastedCard() { //todo to resources
        Alert alert  = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(App.bundle.getString("returnedCardTitle"));
        alert.setHeaderText("Vyhodil jsi kartu z okna, nebo jsi neměl jinou možnost.");
        alert.setContentText(App.bundle.getString("returnedCardText") + " : ");
        alert.showAndWait();
    }
}
