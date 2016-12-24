package gui;

import game.Card;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameController {
    
    public Label player1Name;
    public VBox player1_cards;
    public Label player2_Name;
    public VBox player2_cards;
    public Label player3_name;
    public BorderPane player1;
    public BorderPane player2;
    public BorderPane player3;
    public VBox player3_cards;
    public VBox playerMe_cards;
    public Label playerMeName;
    public BorderPane playerMe;

    public void onClose(ActionEvent actionEvent) {

    }
}
