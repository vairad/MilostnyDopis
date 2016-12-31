package gui;

import constants.Constants;
import constants.PlayerPosition;
import game.Card;
import game.Game;
import game.Player;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
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
    private ResourceBundle bundle;
    private GameController controller;
    private List<String> statusMessages;

    private static final Duration TRANSLATE_DURATION = Duration.seconds(Constants.TRANSLATE_DURATION);

    private TranslateTransition translationMyCard;
    private TranslateTransition translationSecondCard;

    private RotateTransition rotationPointer;
    private TranslateTransition translatePointer;

    private boolean myCardChosen;
    private boolean secondCardChosen;
    private Player chosenPlayer;

    GameWindow(GameRecord gameRecord){
        statusMessages = new LinkedList<>();


        Message msg = new Message(Event.STA, MessageType.game, gameRecord.getUid());
        NetService.getInstance().sender.addItem(msg);

        stage = new Stage();

        loadView();
        statusMessages.add(bundle.getString("statusMessages"));
        statusMessages.add(bundle.getString("connectGame")+ " " +gameRecord.getUid());

        appendStatusMessages();


        createTransitions();
        createListeners();

        stage.setMinHeight(600);
        stage.setMinWidth(800);
    }

    private double getChosenCardStep(){
        return stage.getHeight() / 10.0;
    }

    private void moveMyCardToCenter(){
        translationMyCard.setToY( - getChosenCardStep());
        translationMyCard.playFromStart();
        myCardChosen = true;
    }
    
    private void moveMyCardHome(){
        translationMyCard.setToX(0);
        translationMyCard.setToY(0);
        translationMyCard.playFromStart();
        myCardChosen = false;
    }

    private void moveSecondCardToCenter(){
        translationSecondCard.setToY( - getChosenCardStep());
        translationSecondCard.playFromStart();
        secondCardChosen = true;
    }


    private void moveSecondCardHome(){
        translationSecondCard.setToX(0);
        translationSecondCard.setToY(0);
        translationSecondCard.playFromStart();
        secondCardChosen = false;
    }

    private void createListeners() {
        controller.myCard.setOnMouseClicked(event -> {
            if(event.getClickCount() == Constants.CARD_USE_CLICK_COUNT){
                resolveMyCardState();
                resolveChosenCard();
            }
        });
        controller.secondCard.setOnMouseClicked(event -> {
            if(event.getClickCount() == Constants.CARD_USE_CLICK_COUNT){
                resolveSecondCardState();
                resolveChosenCard();
            }
        });

        controller.player1.setOnMouseClicked(event -> {
           //todo fill chosenPlayer =
            controller.chosenPlayer.setText(controller.player1_name.getText());
        });
        controller.player2.setOnMouseClicked(event -> {
            //todo fill chosenPlayer =
            controller.chosenPlayer.setText(controller.player2_name.getText());
        });
        controller.player3.setOnMouseClicked(event -> {
            //todo fill chosenPlayer =
            controller.chosenPlayer.setText(controller.player3_name.getText());
        });
        controller.playerMe.setOnMouseClicked(event -> {
            //todo fill chosenPlayer =
            controller.chosenPlayer.setText(controller.playerMeName.getText());
        });
    }

    private void resolveChosenCard() {
        controller.playButton.setVisible(false);
        controller.chosenPlayer.setVisible(false);

        if(myCardChosen && !secondCardChosen){
            if(Card.needElectPlayer(controller.myCard.getCard())){
                chosenPlayer = null;
                controller.chosenPlayer.setText("");
                controller.chosenPlayer.setVisible(true);
            }
            controller.playButton.setVisible(true);

        }else if(!myCardChosen && secondCardChosen){
            if(Card.needElectPlayer(controller.secondCard.getCard())){
                chosenPlayer = null;
                controller.chosenPlayer.setText("");
                controller.chosenPlayer.setVisible(true);
            }
            controller.playButton.setVisible(true);

        }else if(!myCardChosen && !secondCardChosen){
            controller.playButton.setVisible(false);
            controller.chosenPlayer.setVisible(false);
        }else{
            logger.error("Nesmyslny stav zvolené karty");
        }

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
        translationMyCard = new TranslateTransition(TRANSLATE_DURATION, controller.myCard);
        translationSecondCard = new TranslateTransition(TRANSLATE_DURATION, controller.secondCard);

        rotationPointer = new RotateTransition(TRANSLATE_DURATION, controller.pointer);
        rotationPointer.setAxis(Rotate.Z_AXIS);
        translatePointer = new TranslateTransition(TRANSLATE_DURATION, controller.pointer);
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

    void setUpPlayers() {
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

    void updateCards() {
        controller.myCard.setCard(Player.getLocalPlayer().getMyCard());
        controller.secondCard.setCard(Player.getLocalPlayer().getSecondCard());
        controller.myCard.getParent().requestLayout();
    }

    void movePointerTo(PlayerPosition position){
        double scaleFactor = 5;
        double randomRotation = 360 * (Constants.random.nextInt() % 4);
        switch (position){
            case CENTER:
                rotationPointer.setToAngle(45 + randomRotation);
                translatePointer.setToX(0);
                translatePointer.setToY( - controller.sharedPlace.getHeight()/scaleFactor);
                break;
            case LEFT:
                rotationPointer.setToAngle(45 + 90 + 90 + 90 + randomRotation);
                translatePointer.setToY(0);
                translatePointer.setToX(- controller.sharedPlace.getWidth()/scaleFactor);
                break;
            case LOCAL:
                rotationPointer.setToAngle(45 + 90 + 90 + randomRotation);
                translatePointer.setToX(0);
                translatePointer.setToY(controller.sharedPlace.getHeight()/scaleFactor);
                break;
            case RIGHT:
                rotationPointer.setToAngle(45 + 90 + randomRotation);
                translatePointer.setToY(0);
                translatePointer.setToX(controller.sharedPlace.getWidth()/scaleFactor);
                break;
            default:
                logger.error("Unimplemented player position");
                return;
        }
        rotationPointer.playFromStart();
        translatePointer.playFromStart();
    }

    public void fillHelp(Card card) {
        controller.helpTitle.setText(CardControl.getCardText(card));
        controller.helpText.setText(CardControl.getCardHelp(card));
    }
}
