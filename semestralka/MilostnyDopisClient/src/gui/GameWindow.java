package gui;

import game.Game;
import game.Player;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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

    public GameWindow(GameRecord gameRecord){
        statusMessages = new LinkedList<>();


        Message msg = new Message(Event.STA, MessageType.game, gameRecord.getUid());
        NetService.getInstance().sender.addItem(msg);

        stage = new Stage();

        loadView();
        statusMessages.add(bundle.getString("statusMessages"));
        statusMessages.add(bundle.getString("connectGame")+ " " +gameRecord.getUid());

        appendStatusMessages();

        stage.setMinHeight(300);
        stage.setMinWidth(300);
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
}
