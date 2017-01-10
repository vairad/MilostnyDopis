package gui;

import constants.Constants;
import constants.PlayerPosition;
import game.Card;
import game.Game;
import game.Player;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
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

import static gui.App.hideWindow;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameWindow extends Stage {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(GameWindow.class.getName());

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
        super();
        statusMessages = new LinkedList<>();

        Message msg = new Message(Event.STA, MessageType.game, gameRecord.getUid());
        NetService.getInstance().sender.addItem(msg);


        loadView(gameRecord.getUid());
        statusMessages.add(bundle.getString("statusMessages"));
        statusMessages.add(bundle.getString("connectGame")+ " " +gameRecord.getUid());

        appendStatusMessages();

        createTransitions();
        createListeners();

        initTable();

        setOnCloseRequest( event -> hideWindow());

        setMinHeight(Constants.MINH_GAME);
        setMinWidth(Constants.MINW_GAME);
    }

    private double getChosenCardStep(){
        return getHeight() / 10.0;
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
                if(Player.getLocalPlayer().haveToken()){
                    resolveMyCardState();
                    resolveChosenCard();
                }
            }
        });
        controller.secondCard.setOnMouseClicked(event -> {
            if(event.getClickCount() == Constants.CARD_USE_CLICK_COUNT){
                if(Player.getLocalPlayer().haveToken()) {
                    resolveSecondCardState();
                    resolveChosenCard();
                }
            }
        });

        controller.player1.setOnMouseClicked(event -> {
            chosenPlayer = controller.player1.getPlayer();
            controller.chosenPlayer.setText(chosenPlayer.getDisplayName());
        });
        controller.player2.setOnMouseClicked(event -> {
            chosenPlayer = controller.player2.getPlayer();
            controller.chosenPlayer.setText(chosenPlayer.getDisplayName());
        });
        controller.player3.setOnMouseClicked(event -> {
            chosenPlayer = controller.player3.getPlayer();
            controller.chosenPlayer.setText(chosenPlayer.getDisplayName());
        });
        controller.playerMe.setOnMouseClicked(event -> {
            chosenPlayer = controller.playerMe.getPlayer();
            controller.chosenPlayer.setText(chosenPlayer.getDisplayName());
        });


        controller.helpPlace.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                controller.helpText.setWrappingWidth(controller.helpPlace.getWidth() - 20);
            }
        });
        controller.helpPlace.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                App.fillHelp(null);
            }
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


    void appendStatusMessages(){
        controller.appendStatus(statusMessages);
        statusMessages.clear();
    }

    public void addStatusMessage(String message){
        statusMessages.add(message);
    }

    private void loadView(String gameName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            bundle = App.bundle;
            fxmlLoader.setResources(bundle);

            Parent root = fxmlLoader.load(GameWindow.class.getResource("gameScreen.fxml").openStream());

            controller = fxmlLoader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(GameWindow.class.getResource("app.css").toExternalForm());

            setTitle(gameName);
            setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setMyTitle(String gameName) {
        setTitle(bundle.getString("game") + " " + gameName
                + " " + bundle.getString("onServer") + " "
                + NetService.getInstance().getServerName());
    }

    void setUpPlayers() {
        for (Player player: Game.getPlayers()) {
            switch (player.getDisplay_order()){
                case LOCAL:
                    Player.updateLocalPlayer(player);
                    controller.playerMe.setPlayer(Player.getLocalPlayer());
                    break;
                case LEFT:
                    controller.player1.setPlayer(player);
                    break;
                case CENTER:
                    controller.player2.setPlayer(player);
                    break;
                case RIGHT:
                    controller.player3.setPlayer(player);
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
        if(card == null){
            controller.helpTitle.setText(bundle.getString("defHelpTitle"));
            controller.helpText.setText(bundle.getString("defHelpText"));
            return;
        }
        controller.helpTitle.setText(CardControl.getCardText(card));
        controller.helpText.setText(CardControl.getCardHelp(card));
    }

    public void PlayCard() {
        Card playCard = getChosenCard();
        Card tip = null;
        if(playCard == Card.GUARDIAN){
            tip = DialogFactory.guardianChose();
            if(tip == null){
                DialogFactory.alertError(bundle.getString("noGuardianTitle")
                                        ,bundle.getString("noGuardianHeadline")
                                        ,bundle.getString("noGuardianText"));
                return;
            }
        }
        if(Card.needElectPlayer(playCard) && chosenPlayer == null){
            DialogFactory.alertError(bundle.getString("noPlayerTitle")
                    ,bundle.getString("noPlayerHeadline")
                    ,bundle.getString("noPlayerText"));
            return;
        }
        if(Card.needElectPlayer(playCard)
                && chosenPlayer.equals(Player.getLocalPlayer())
                && playCard != Card.PRINCE){
            DialogFactory.alertError(bundle.getString("noMeTitle")
                    ,bundle.getString("noMeHeadline")
                    ,bundle.getString("noMeText"));
            return;
        }

        if(!Card.needElectPlayer(playCard)){
            chosenPlayer = null;
        }
        sendCardToServer(playCard, chosenPlayer, tip);
        playChosenCard();
        controller.playerMe.requestLayout();
    }

    private void sendCardToServer(Card playCard, Player chosenPlayer, Card tip) {
        String messageS = "";
        messageS += playCard.getValue();
        if(chosenPlayer != null){
            messageS += "&";
            messageS += chosenPlayer.getServerUid();
            if(tip != null){
                messageS += "&";
                messageS += tip.getValue();
            }
        }

        Message msg = new Message(Event.PLA, MessageType.game, messageS );
        NetService.getInstance().sender.addItem(msg);
    }

    public Card getChosenCard() {
        Card c = null;
        if(myCardChosen){
            c = controller.myCard.getCard();
        }
        if(secondCardChosen){
            c = controller.secondCard.getCard();
        }
        return c;
    }

    public Card playChosenCard() {
        Card c = null;
        if(myCardChosen){
            c = controller.myCard.getCard();
            Player.getLocalPlayer().playMyCard();
            controller.myCard.setCard(controller.secondCard.getCard());
        }
        if(secondCardChosen){
            Player.getLocalPlayer().playSecondCard();
            c = controller.secondCard.getCard();
        }
        controller.secondCard.setCard(Card.NONE);

        secondCardChosen = false;
        myCardChosen = false;
        chosenPlayer = null;
        moveMyCardHome();
        moveSecondCardHome();
        resolveChosenCard();

        return c;
    }

    /**
     * Method update card box for each player
     */
    public void addCard(){
        controller.player1.updateCards();
        controller.player2.updateCards();
        controller.player3.updateCards();
        controller.playerMe.updateCards();
    }

    /**
     * Method update enable state for each player
     */
    public void updatePlayers() {
        controller.player1.update();
        controller.player2.update();
        controller.player3.update();
        controller.playerMe.update();
    }

    public void setRounds(int round, int roundCount) {
        controller.roundLabel.setText(" "+round);
        controller.roundCountLabel.setText(" ("+roundCount+")");
    }

    private void initTable(){
        TableColumn playerColumn = new TableColumn(App.bundle.getString("playerPoints"));
        TableColumn pointsColumn = new TableColumn(App.bundle.getString("pointsPoints"));

        playerColumn.prefWidthProperty().bind(controller.gameResults.widthProperty().divide(3).multiply(1.8)); // w * 1/4
        pointsColumn.prefWidthProperty().bind(controller.gameResults.widthProperty().divide(3)); // w * 2/4

        playerColumn.setCellValueFactory(
                new PropertyValueFactory<Player, String>("nick")
        );
        pointsColumn.setCellValueFactory(
                new PropertyValueFactory<Player, Integer>("points")
        );

        controller.gameResults.getColumns().addAll(playerColumn, pointsColumn);
    }

    public void updateScore() {
        ObservableList<Player> data = FXCollections.observableArrayList();
        data.addAll(Game.getPlayers());

        controller.gameResults.setItems(data);
        controller.gameResults.refresh();
    }
}
