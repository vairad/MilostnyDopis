package gui;

import game.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameController {

    @FXML public BorderPane player1;
    @FXML public Label player1_name;
    @FXML public VBox player1_cards;

    @FXML public BorderPane player2;
    @FXML public Label player2_name;
    @FXML public VBox player2_cards;

    @FXML public BorderPane player3;
    @FXML public Label player3_name;
    @FXML public VBox player3_cards;

    @FXML public BorderPane playerMe;
    @FXML public VBox playerMe_cards;
    @FXML public Label playerMeName;
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

    public void setLocalPlayer(Player localPlayer) {
        playerMe.setDisable(false);
        playerMeName.setText(localPlayer.getNick() + "(" +localPlayer.getServerUid() + ")");
    }

    public void setPlayer1(Player player) {
        player1.setDisable(false);
        player1_name.setText(player.getNick() + "(" +player.getServerUid() + ")");
    }

    public void setPlayer2(Player player) {
        player2.setDisable(false);
        player2_name.setText(player.getNick() + "(" +player.getServerUid() + ")");
    }

    public void setPlayer3(Player player) {
        player3.setDisable(false);
        player3_name.setText(player.getNick() + "(" +player.getServerUid() + ")");
    }

    public void onSubmit(ActionEvent actionEvent) {
        DialogFactory.alertError("Něco", "Titulek", "Text");
    }

}
