package gui;

import game.Card;
import game.Game;
import game.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 2.1.17.
 */
public class PlayerControl extends BorderPane {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(PlayerControl.class.getName());

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
        checkPlayerToken();
        updateCardsBox();

    }

    private void updateCardsBox() {
        cards.getChildren().clear();
        player.acquireCollection();
        for (Card card: player.getPlayedCards()) {
            cards.getChildren().add(new CardControl(card));
        }
        player.releaseCollection();
    }

    private void checkPlayerToken() {
        if(player.haveToken()){
            Platform.runLater(() -> App.moveTokenTo(player));
        }
    }

    private void checkPlayerAlive() {
        if(player.isAlive()){
            setDisable(false);
        }else{
            setDisable(true);
        }
    }

    public void update(){
        logger.debug("Update players state");
        try {
            player = Game.getPlayer(player.getServerUid());
            checkPlayerAlive();
            checkPlayerToken();
        }catch (NullPointerException e){
            logger.trace("Null player");
        }
    }

    public Player getPlayer(){
        return player;
    }

    public void updateCards() {
        logger.debug("Update players state");
        try {
            player = Game.getPlayer(player.getServerUid());
            updateCardsBox();
        }catch (NullPointerException e){
            logger.trace("Null player");
        }
    }
}
