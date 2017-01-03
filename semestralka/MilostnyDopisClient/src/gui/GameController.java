package gui;

import game.Game;
import game.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;

import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameController {

    @FXML public PlayerControl player1;
    @FXML public PlayerControl player2;
    @FXML public PlayerControl player3;

    @FXML public PlayerControl playerMe;


    @FXML public BorderPane myHand;

    @FXML public ScrollPane helpPlace;
    @FXML public BorderPane help;
    @FXML public Label helpTitle;
    @FXML public Text helpText;

    @FXML public ListView<String> gameStatus;

    @FXML public CardControl myCard;
    @FXML public CardControl secondCard;

    @FXML public StackPane sharedPlace;
    @FXML public Polygon pointer;

    @FXML public Button playButton;
    @FXML public TextField chosenPlayer;


    public void onClose(ActionEvent actionEvent) {
        //todo co se stane na konci hry
    }

    void appendStatus(List<String> message){
        gameStatus.getItems().addAll(message);
    }


    public void onSubmit(ActionEvent actionEvent) {
        DialogFactory.alertError("Předávám token", "Titulek", "Text");
        Player.getLocalPlayer().giveToken();
        Message msg = new Message(Event.TOK, MessageType.game, Game.getUid());
        NetService.getInstance().sender.addItem(msg);
    }

}
