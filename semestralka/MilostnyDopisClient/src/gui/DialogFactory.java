package gui;

import game.Card;
import game.Player;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class DialogFactory {

    private static void setUpCssToDialog(DialogPane dialogPane){
        dialogPane.getStylesheets().add(
                DialogFactory.class.getResource("app.css").toExternalForm());
    }

    /**
     * Create and show alert dialog with defined parameters
     * @param title window header
     * @param headline error headline
     * @param text long description
     */
    public static void alertError(String title, String headline, String text ){
        Alert alert  = new Alert(Alert.AlertType.ERROR);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(title);
        alert.setHeaderText(headline);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public static boolean yesNoQuestion(String title, String question) {
        Alert alert  = new Alert(Alert.AlertType.CONFIRMATION);
        setUpCssToDialog(alert.getDialogPane());
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
        setUpCssToDialog(dialog.getDialogPane());
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
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("returnedCardTitle"));
        alert.setHeaderText(App.bundle.getString("returnedCardHeadline"));
        alert.setContentText(App.bundle.getString("returnedCardText") + " : " + card);
        alert.showAndWait();
    }

    public static void resultDialog(Card playedCard, Player playerWhoPlays, boolean isMyTarget, String cardResult) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle("Výsledek karty");
        if(!isMyTarget){
            alert.setHeaderText(App.bundle.getString("foreignCard"));
        } else{
            alert.setHeaderText(App.bundle.getString("myCard"));
        }
        String text = App.resolvePlayedCardResult(playedCard, playerWhoPlays, cardResult);
        alert.setContentText(text);
        alert.showAndWait();

    }

    public static int gameCountDialog() {
        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(3);
        choices.add(5);
        choices.add(7);
        choices.add(9);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, choices);
        setUpCssToDialog(dialog.getDialogPane());
        dialog.setTitle(App.bundle.getString("gameCountTitle"));
        dialog.setHeaderText(App.bundle.getString("gameCountQuestion"));
        dialog.setContentText(App.bundle.getString("gameCountText"));

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return -1;
    }

    public static int playerCountDialog() {
        List<Integer> choices = new ArrayList<>();
        choices.add(2);
        choices.add(3);
        choices.add(4);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(2, choices);
        setUpCssToDialog(dialog.getDialogPane());
        dialog.setTitle(App.bundle.getString("playerCountTitle"));
        dialog.setHeaderText(App.bundle.getString("playerCountQuestion"));
        dialog.setContentText(App.bundle.getString("playerCountText"));

        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return -1;
    }

    public static void roundEndDialog(List<Player> winners, Card winCard){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        setUpCssToDialog(alert.getDialogPane());
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
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("endOfGame"));
        alert.setHeaderText(App.bundle.getString("endOfGameHeader") + "\n" +App.getWinner());
        alert.setContentText(App.bundle.getString("endOfGameText") + "\n" + App.getResult());
        alert.showAndWait();
    }

    public static void wastedCard(Card card) {
        Alert alert  = new Alert(Alert.AlertType.WARNING);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("wastedCardTitle"));
        alert.setHeaderText(App.bundle.getString("wastedCardHeadline"));
        alert.setContentText(App.bundle.getString("wastedCardText") + " : " + card);
        alert.showAndWait();
    }

    public static void loginInfo(UserRecord value) {
        Alert alert  = new Alert(Alert.AlertType.INFORMATION);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("loginTitle"));
        alert.setHeaderText(App.bundle.getString("loginHeadline"));
        alert.setContentText(value.toString());
        alert.showAndWait();
    }

    public static void differentGame(GameRecord game) {
        Alert alert  = new Alert(Alert.AlertType.WARNING);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("diffGameTitle"));
        alert.setHeaderText(App.bundle.getString("diffGameHeadline"));
        alert.setContentText(game.toString());
        alert.showAndWait();
    }

    public static void messagesResult(long sendBytes, long recvBytes, long reconnection) {
        Alert alert  = new Alert(Alert.AlertType.INFORMATION);
        setUpCssToDialog(alert.getDialogPane());
        alert.setTitle(App.bundle.getString("netResult"));
        String results = App.bundle.getString("send") +" : " + sendBytes + "B" + "\n";
        results += App.bundle.getString("received") +" : " + recvBytes + "B" + "\n";
        results += App.bundle.getString("reconnection") +" : " + reconnection + "x" + "\n";
        alert.setHeaderText(results);
        alert.showAndWait();
    }
}
