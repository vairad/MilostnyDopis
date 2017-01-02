package gui;

import game.Card;
import game.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 2.1.17.
 */
public class PlayerControl extends BorderPane {

    @FXML private VBox cards;
    @FXML private Label name;
    private Player player;

    private ResourceBundle bundle;

    public PlayerControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "playerControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        bundle = App.bundle;
        fxmlLoader.setResources(App.bundle);

        getStylesheets().add(CardControl.class.getResource("playerControl.css").toExternalForm());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setPlayer(Player player){
        this.player = player;
        if(player == null){
            setDisable(true);
            name.setText(bundle.getString("playerPlaceholder"));
            return;
        }
        name.setText(player.getDisplayName());
        checkPlayerAlive();
        cards.getChildren().clear();
        for (Card card: player.getPlayedCards()) {
            cards.getChildren().add(new CardControl(card));
        }
    }

    private void checkPlayerAlive() {
        if(player.isAlive()){
            setDisable(false);
        }else{
            setDisable(true);
        }
    }

    public Player getPlayer(){
        return player;
    }
}
