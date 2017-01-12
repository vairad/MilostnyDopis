package gui;

import game.Card;
import game.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameController{

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
    @FXML public TableView<Player> gameResults;
    @FXML public Label roundLabel;
    @FXML public Label roundCountLabel;
    @FXML public ComboBox<Card> chosenCard;


    public void onSubmit(ActionEvent actionEvent) {
        Platform.runLater(() -> App.win.PlayCard());
    }

}
