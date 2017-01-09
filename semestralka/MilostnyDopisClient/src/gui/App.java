package gui;

import game.Card;
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
import message.MessageHandler;
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

    public static GameWindow win = null;

    static Thread loginWorker;

    private Stage stage;

    public static UserRecord userRecord = null;

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
            if(gameRecord.isStarted()){
                continue;
            }
            TreeItem<GameRecord> item = new TreeItem<GameRecord> (gameRecord);
            rootItem.getChildren().add(item);
        }
        TreeView<GameRecord> tree = controller.getTreeWiew();
        tree.setRoot(rootItem);
        tree.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2)
            {   GameRecord item;
                try {
                    item = tree.getSelectionModel().getSelectedItem().getValue();
                }catch (NullPointerException e){
                    logger.debug("nothing selected");
                    return;
                }
                if(item == null){
                    logger.debug("null selected");
                    return;
                }
                logger.debug("Byla zvolena hra pro přihlášení:" + item);
                boolean result = DialogFactory.yesNoQuestion(bundle.getString("loginGameQTitle")
                                            , bundle.getString("loginGameQ") + " " + item);
                if(!result){
                    logger.trace("Uživatel zrušil přihlašování do hry: " + item);
                    return;
                }
                if(!Player.getLocalPlayer().isLogged()){
                    DialogFactory.alertError(bundle.getString("noLogged")
                                            , bundle.getString("noLoggedHeadline")
                                            , bundle.getString("noLoggedText"));
                    return;
                }

                Platform.runLater(() -> App.newGame(item));

                Message msg = new Message(Event.COD, MessageType.game, item.getUid());
                NetService.getInstance().sender.addItem(msg);
                logger.trace("Proveden pokus o přihlášení do hry: " + item);


                }
        }); // end of event handler definition

        App.enableTreeView();
    }


    //======================== REMOTE EVENTS ================================

    public static void showMessagesGameWindow(){
        win.appendStatusMessages();
    }

    public static void setUpPlayers(){
        win.setUpPlayers();
    }

    public static void newGame(GameRecord gameRecord) {
        logger.debug("start method");
        Game.initialize(gameRecord);
        win = new GameWindow(gameRecord);
    }

    public static void showGameWindow() {
        win.show();
    }

    public static void userLogged(){
        controller.stopProgress();
        controller.loggedForm();
    }

    private static void enableTreeView() {
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


    public static void updateCards() {
        if(win != null){
            win.updateCards();
        }
    }

    public static void fillHelp(Card card) {
        App.win.fillHelp(card);
    }

    public static void moveTokenTo(Player player) {
        App.win.movePointerTo(player.getDisplay_order());
    }

    public static String resolvePlayedCardResult(Card playedCard, Player playerWhoPlays, String cardResult) {
        String text = "";
        if(!playerWhoPlays.equals(Player.getLocalPlayer())){
            text += bundle.getString("playerPlays") + ": " + playerWhoPlays.getDisplayName();
            text += "\n";
        }
        text += bundle.getString("playedCard") + ": " + playedCard;
        text += "\n";

        text += resoveCardResult(playedCard, cardResult);

        return text;
    }

    private static String resoveCardResult(Card playedCard, String cardResult) {
        String text = null;
        switch (playedCard){
            case GUARDIAN:
                text = "Chyba";
                if(cardResult.equals("KILL")){
                    text = App.bundle.getString("guardianKill");
                }
                if(cardResult.equals("MISS")){
                    text = App.bundle.getString("guardianMiss");
                }
                break;
            case PRIEST:
                text = App.bundle.getString("priestResult") + ": ";
                try {
                    text += Card.getCardFromInt(Integer.parseInt(cardResult)).toString();
                }catch (NumberFormatException | NullPointerException e){
                    text += App.bundle.getString("CARD9");
                }
                break;
            case BARON:
                text = App.bundle.getString("baronResult");
                text += "\n";
                text += resolveBaronResult(cardResult);
                break;
            case KOMORNA:
                text = App.bundle.getString("komornaResult");
                break;
            case PRINCE:
                text = App.bundle.getString("princeResult") + ": ";
                try {
                    text += Card.getCardFromInt(Integer.parseInt(cardResult)).toString();
                }catch (NumberFormatException | NullPointerException e){
                    text += App.bundle.getString("CARD9");
                }
                break;
            case KING:
                text = App.bundle.getString("kingResult");
                break;
            case COUNTESS:
                text = App.bundle.getString("countessResult");
                break;
            case PRINCESS:
                text = App.bundle.getString("princessEnd");
                break;
            default:
                logger.error("Snaha vyplnit výsledek karty neexistující kartou");
                text = "Asi špatněj oddíl nic se u nás nehraje";
                break;
        }
        return text;
    }

    private static String resolveBaronResult(String cardResult) {
        String[] baronParts = cardResult.split("@@");
        String text = "";
        if(baronParts.length == 5 && baronParts[0].equals("RESULT")){
            try {
                Card winCard = Card.getCardFromInt(Integer.parseInt(baronParts[3]));
                Player winner = Game.getPlayer(baronParts[4]);
                text += App.bundle.getString("winner")
                        + "\t"
                        + winner.getDisplayName()
                        + "\t"
                        + App.bundle.getString("withCard")
                        + winCard;
                text += "\n";

                Card loseCard = Card.getCardFromInt(Integer.parseInt(baronParts[1]));
                Player loser = Game.getPlayer(baronParts[2]);
                text += App.bundle.getString("loser")
                        + "\t"
                        + loser.getDisplayName()
                        + "\t"
                        + App.bundle.getString("withCard")
                        + loseCard;
            }catch (NullPointerException | NumberFormatException e){
                logger.error("Chyba ve zprávě výsledku baronů");
                text = "Chyba";
            }
        }
        return text;
    }

    private static void sendLogin(){
        logger.debug("Start method");
        String messageS = controller.checkNickname();
        if(messageS == null){
            logger.trace("no nickname");
            return;
        }
        Message msg = new Message(Event.ECH, MessageType.login, messageS);
        NetService.getInstance().sender.addItem(msg);
    }

    public static void connect() {
        boolean result = false;
        if(!NetService.isRunning()){
           result = connectServer();    //pripoj server
        }
        if(!result){  // když se nepovedlo připojení uvolni a skonči
            NetService.getInstance().destroy();
            controller.prepareLogin();
            controller.stopProgress();
            return;
        }
        if (Thread.currentThread().isInterrupted()) {
            controller.prepareLogin(); // když jsi přeušen vše uvolni a skonči
            controller.stopProgress();
            return;
        }
        sendLogin();
    }


    public static void logout(){
        if(App.loginWorker.isAlive()){
            App.loginWorker.interrupt();
        }
        try {
            if(Player.getLocalPlayer().isLogged()){
                Message msg = new Message(Event.OUT, MessageType.login, Player.getLocalPlayer().getServerUid() );
                for(int i = 0; i < 10; i++) {
                    NetService.getInstance().sender.addItem(msg);
                }
            }
        }catch (NullPointerException e){
            logger.trace("Uzivatel nebyl prihlasen.");
        }

        controller.prepareLogin();
        controller.stopProgress();
        NetService.getInstance().destroy();
    }


    static boolean connectServer() {
        logger.debug("Start method");

        int resultPort;
        try {
            resultPort = NetService.checkPort(controller.port.getText());
        }catch (NumberFormatException e){
            logger.error("Error port number", e);
            resultPort = -1;
        }

        if(resultPort == -1){
            logger.error("Wrong range of port");

            DialogFactory.alertError( bundle.getString("portErrorTitle")
                    , bundle.getString("portErrorHeadline")
                    , bundle.getString("portErrorText"));
            return false;
        }

        try{
            String text = bundle.getString("connectionTry");
            Platform.runLater(() -> {controller.setStatusText(text);});

            NetService.getInstance().setup(controller.getAddress(), resultPort);
            NetService.getInstance().initialize();

            logger.trace("Iinit MessageHandler");
            NetService.messageHandler = new MessageHandler();
            logger.trace("start messageHandler");
            NetService.messageHandler.start();
            logger.trace("MessageHandler started");

        }catch (IOException e){
            String text = bundle.getString("notConnected") +" " + e.getLocalizedMessage();
            Platform.runLater(() -> {controller.setStatusText(text);});
            logger.error("IO Error", e);
            return false;
        }

        String text = bundle.getString("loginInProgress");
        Platform.runLater(() -> {controller.setStatusText(text);});
        return true;
    }

    public static void login(UserRecord value) {
        controller.disableForm();
        controller.startProgress();
        controller.setUpUser(value);

        boolean result = false;
        if(!NetService.isRunning()){
            result = connectServer();    //pripoj server
        }
        if(!result){  // když se nepovedlo připojení uvolni a skonči
            NetService.getInstance().destroy();
            controller.prepareLogin();
            controller.stopProgress();
            return;
        }

        userRecord = value;
        Message msg = new Message(Event.COD, MessageType.login, value.getId().trim());
        NetService.getInstance().sender.addItem(msg);
    }

    public static void refreshOldPlayers() {
        controller.userControl.refresh();
    }

}
