package gui;

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

    public GameWindow(GameRecord gameRecord){
        Message msg = new Message(Event.STA, MessageType.game, gameRecord.getUid());
        NetService.getInstance().sender.addItem(msg);
    }

    public void show(){
        logger.debug("start Window");
        stage = new Stage();

        loadView();

        controller.appendStatus(bundle.getString("statusMessages"));



        stage.setMinHeight(300);
        stage.setMinWidth(500);
       // stage.setMaximized(true);
        stage.show();
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
}
