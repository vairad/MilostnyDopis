package gui;

import game.Game;
import game.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static {
        System.setProperty("log4j.configurationFile","log-conf.xml");
    }

    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(App.class.getName());

    private Stage stage;

    protected static ResourceBundle bundle;
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        loadView(new Locale("cs", "CZ"));

        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.show();
    }

    private void loadView(Locale locale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            bundle = ResourceBundle.getBundle("Texts", locale);
            fxmlLoader.setResources(bundle);

            Parent root = fxmlLoader.load(App.class.getResource("startScreen.fxml").openStream());

            controller = (Controller) fxmlLoader.getController();

            Scene scene = new Scene(root, 300, 500);
            scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());

            stage.setTitle(bundle.getString("TITLE"));
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception{
        NetService.getInstance().destroy();
        NetService.getInstance().joinThreads();

        super.stop();
    }

    public static void fillTree(List<GameRecord> gameRecords){
        TreeItem<GameRecord> rootItem = new TreeItem<GameRecord> (new GameRecord(NetService.serverName, true));
        rootItem.setExpanded(true);
        for (GameRecord gameRecord : gameRecords) {
            TreeItem<GameRecord> item = new TreeItem<GameRecord> (gameRecord);
            rootItem.getChildren().add(item);
        }
        TreeView<GameRecord> tree = controller.getTreeWiew();
        tree.setRoot(rootItem);
        tree.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2)
            {
                GameRecord item = tree.getSelectionModel().getSelectedItem().getValue();
                logger.debug("Byla zvolena hra pro přihlášení:" + item);
                boolean result = DialogFactory.yesNoQuestion(bundle.getString("loginGameQTitle")
                                            , bundle.getString("loginGameQ") + " " + item);
                if(!result){
                    logger.trace("Uživatel zrušil přihlašování do hry: " + item);
                    return;
                }
                if(!Player.getInstance().isLogged()){
                    DialogFactory.alertError(bundle.getString("noLogged")
                                            , bundle.getString("noLoggedHeadline")
                                            , bundle.getString("noLoggedText"));
                    return;
                }

                Message msg = new Message(Event.COD, MessageType.game, item.getUid());
                NetService.getInstance().sender.addItem(msg);
                logger.trace("Proveden pokus o přihlášení do hry: " + item);


                }
        }); // end of event handler definition

        App.disableTreeView();
    }


    //======================== REMOTE EVENTS ================================

    public static void newGame(GameRecord gameRecord) {
        logger.debug("start method");
        Game.initialize(gameRecord);
        GameWindow win = new GameWindow();
        win.show();
    }

    public static void userLogged(){
        controller.loggedForm();
    }

    private static void disableTreeView() {
        controller.enableGameMenu();
    }


    //=====================================================================================================
    //=========================================================
    //===========================
    // MAIN METHOD
    public static void main(String[] args) {
        logger.info("Zacatek programu");

        App.launch(args);
        logger.info("Konec programu");
    }

}
