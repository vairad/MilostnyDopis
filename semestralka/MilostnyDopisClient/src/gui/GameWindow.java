package gui;

import constants.Constants;
import game.Game;
import game.Player;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameWindow extends Window {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(GameWindow.class.getName());

    private Stage stage;
    ResourceBundle bundle;
    private GameController controller;
    private List<String> statusMessages;

    private static final Duration TRANSLATE_DURATION = Duration.seconds(Constants.TRANSLATE_DURATION);

    private TranslateTransition transitionMyCard;
    private TranslateTransition transitionSecondCard;
    private boolean myCardChosen;
    private boolean secondCardChosen;

    public GameWindow(GameRecord gameRecord){
        statusMessages = new LinkedList<>();


        Message msg = new Message(Event.STA, MessageType.game, gameRecord.getUid());
        NetService.getInstance().sender.addItem(msg);

        stage = new Stage();

        loadView();
        statusMessages.add(bundle.getString("statusMessages"));
        statusMessages.add(bundle.getString("connectGame")+ " " +gameRecord.getUid());

        appendStatusMessages();


        createTransitions();
        createDBClickListeners();

        stage.setMinHeight(600);
        stage.setMinWidth(800);
    }

    private double getChosenCardStep(){
        return stage.getHeight() / 10.0;
    }

    private void moveMyCardToCenter(){
        transitionMyCard.setToY( - getChosenCardStep());
        transitionMyCard.playFromStart();
        myCardChosen = true;
    }
    
    private void moveMyCardHome(){
        transitionMyCard.setToX(0);
        transitionMyCard.setToY(0);
        transitionMyCard.playFromStart();
        myCardChosen = false;
    }

    private void moveSecondCardToCenter(){
        transitionSecondCard.setToY( - getChosenCardStep());
        transitionSecondCard.playFromStart();
        secondCardChosen = true;
    }


    private void moveSecondCardHome(){
        transitionSecondCard.setToX(0);
        transitionSecondCard.setToY(0);
        transitionSecondCard.playFromStart();
        secondCardChosen = false;
    }

    private void createDBClickListeners() {
        controller.myCard.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                resolveMyCardState();
            }
        });
        controller.secondCard.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                resolveSecondCardState();
            }
        });
    }

    private void resolveSecondCardState() {
        if(secondCardChosen){
            moveSecondCardHome();
            secondCardChosen = false;
            return;
        }
        moveSecondCardToCenter();
        moveMyCardHome();
        secondCardChosen = true;
        myCardChosen = false;
    }

    private void resolveMyCardState() {
        if(myCardChosen){
            moveMyCardHome();
            myCardChosen = false;
            return;
        }
        moveMyCardToCenter();
        moveSecondCardHome();
        myCardChosen = true;
        secondCardChosen = false;
    }

    private void createTransitions() {
        transitionMyCard = new TranslateTransition(TRANSLATE_DURATION, controller.myCard);
        transitionSecondCard = new TranslateTransition(TRANSLATE_DURATION, controller.secondCard);
    }


    @Override
    public void show(){
        logger.debug("start Window");

        appendStatusMessages();

        stage.show();
    }

    void appendStatusMessages(){
        controller.appendStatus(statusMessages);
        statusMessages.clear();
    }

    public void addStatusMessage(String message){
        statusMessages.add(message);
    }

    private void loadView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            bundle = App.bundle;
            fxmlLoader.setResources(bundle);

            Parent root = fxmlLoader.load(GameWindow.class.getResource("gameScreen.fxml").openStream());

            controller = fxmlLoader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(GameWindow.class.getResource("app.css").toExternalForm());

            stage.setTitle(bundle.getString("TITLE")
                            + " " + bundle.getString("onServer") + " "
                            + NetService.getInstance().getServerName());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setUpPlayers() {
        for (Player player: Game.getPlayers()) {
            switch (player.getDisplay_order()){
                case LOCAL:
                    controller.setLocalPlayer(player);
                    break;
                case LEFT:
                    controller.setPlayer1(player);
                    break;
                case CENTER:
                    controller.setPlayer2(player);
                    break;
                case RIGHT:
                    controller.setPlayer3(player);
                    break;
            }
        }
    }

    public void updateCards() {
        controller.myCard.setCard(Player.getLocalPlayer().getMyCard());
        controller.secondCard.setCard(Player.getLocalPlayer().getSecondCard());
        controller.myCard.getParent().requestLayout();
    }

    public double getCenterX() {
        return stage.getWidth() / 2.0;
    }
}
