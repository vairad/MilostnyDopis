package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

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

    @FXML public ListView<String> gameStatus;

    public void onClose(ActionEvent actionEvent) {

    }

    public void appendStatus(String message){
        gameStatus.getItems().add(message);
    }
}
