package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

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
    @FXML public TableView gameResults;
    @FXML public Label roundLabel;
    @FXML public Label roundCountLabel;


    void appendStatus(List<String> message){
        gameStatus.getItems().addAll(message);
    }


    public void onSubmit(ActionEvent actionEvent) {
        Platform.runLater(() -> App.win.PlayCard());
    }

}
