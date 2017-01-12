package gui;

import constants.Constants;
import game.Card;
import game.Game;
import game.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import message.Event;
import message.Message;
import message.MessageHandler;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class App extends Application {

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static {
        System.setProperty("log4j.configurationFile","log-conf.xml");
    }

    /** instance loggeru hlavni tridy */
    public static Logger logger = LogManager.getLogger(App.class.getName());

    /** Odkaz na okno se hrou  */
    public static GameWindow win = null;
    /** čítač pokusů o znovupřipojení */
    static long reconnection = 0;
    /** vlákno určené pro zpracování přihlášení */
    private static Thread loginWorker;
    /** Vlákno zpracovávající zápis uživatelů do XML */
    private static Thread xmlWorker;
    /** záznam o uživateli */
    public static UserRecord userRecord = null;
    /** java FX stage tohoto okna */
    private Stage stage;
    /** jazyková lokalizace ... databáze textů */
    static ResourceBundle bundle;
    /** odkaz na kontroler okna */
    private static Controller controller;

    //====================================== METODY PRO PRACI S OKNEM ==========================================

    /**
     * Při vytváření okna nastaví všechny potřebné porměnné atp... "pseudo konsrtuktor"
     * @param primaryStage stage
     * @throws Exception exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        logger.debug("start main window");
        stage = primaryStage;

        Platform.setImplicitExit(false);

        loadView(new Locale("cs", "CZ"));

        stage.setOnCloseRequest( event -> {
            try {
                App.closeApp();
            } catch (Exception e) {
                logger.fatal("Chyba při ukončování aplikace", e);
            }
        });

        stage.setMinHeight(Constants.MINH_APP);
        stage.setMinWidth(Constants.MINW_APP);
        stage.show();
    }

    /**
     * Načte fxml definici prvků v okně a soubor s lokalizací
     * @param locale lokalizace k zobrazení
     */
    private void loadView(Locale locale) {
        logger.debug("load main win View");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            bundle = ResourceBundle.getBundle("Texts", locale);
            fxmlLoader.setResources(bundle);

            Parent root = fxmlLoader.load(App.class.getResource("startScreen.fxml").openStream());

            controller = fxmlLoader.getController();

            Scene scene = new Scene(root, Constants.MINW_APP, Constants.MINH_APP);
            scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());

            stage.setTitle(bundle.getString("TITLE"));
            stage.setScene(scene);
        } catch (IOException ex) {
            logger.fatal("Unnable to load resources for main window", ex);
        }
    }

    /**
     * Zajistí korektní ukončení aplikace
     */
    private static void closeApp(){
        logger.debug("Ukončuji aplikaci");
        if(loginWorker != null){
            loginWorker.interrupt();
        }

        NetService.getInstance().destroy();
        NetService.getInstance().joinThreads();

        closeGameWindow();

        Platform.exit();
    }

    /**
     * Zajistí ukončení herního okna a nastaví pointer na null
     */
    public static void closeGameWindow() {
        try{
            Stage stage = (Stage) App.win.getScene().getWindow();
            Platform.runLater(stage::close);
        }catch (NullPointerException e){
            logger.trace("win was not initialised");
        }
        App.win = null;
    }


    //======================== REMOTE EVENTS ====================================================================

    /**
     * Připraví
     */
    public static void smartFillTree() {
        if(GameRecord.getAllGameRecords() == null){
            controller.fillTree(new LinkedList<>());
        }
        if(controller.isAllGames()){
            logger.trace("is selected");
            Platform.runLater(() -> controller.fillTree(GameRecord.getAllGameRecords()));
        }else {
            logger.trace("is not selected");
            controller.fillTree(GameRecord.getAllConnectableGames());
        }
    }

    public static void setUpPlayers(){
        win.setUpPlayers();
    }

    public static void newGame(GameRecord gameRecord) {
        logger.debug("start method");
        Game.initialize(gameRecord);
        if(win == null){
            win = new GameWindow(gameRecord);
        }else{
            win.show();
            win.toFront();
        }
    }

    public static void showGameWindow() {
        win.show();
    }

    public static void userLogged(){
        controller.stopProgress();
        controller.loggedForm();
        controller.setStatusText(bundle.getString("loggedIn"));
    }


    /**
     * Aktualiuje zobrazené karty v okně
     */
    public static void updateCards() {
        if(win != null){
            win.updateCards();
        }
    }

    static void fillHelp(Card card) {
        App.win.fillHelp(card);
    }

    public static void moveTokenTo(Player player) {
        App.win.movePointerTo(player.getDisplay_order());
    }

    static String resolvePlayedCardResult(Card playedCard, Player playerWhoPlays, String cardResult) {
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
        String text;
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
                        + App.bundle.getString("withCard") + "\t"
                        + winCard;
                text += "\n";

                Card loseCard = Card.getCardFromInt(Integer.parseInt(baronParts[1]));
                Player loser = Game.getPlayer(baronParts[2]);
                text += App.bundle.getString("loser")
                        + "\t"
                        + loser.getDisplayName()
                        + "\t"
                        + App.bundle.getString("withCard")+ "\t"
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

    /**
     * Vyvolá aktualizaci prvku seznamu dříve přihlášehých hráčů
     */
    public static void refreshOldPlayers() {
        controller.refreshUserRecords();
    }

    public static void highlightGame(GameRecord record) {
        // todo highlight element in tree view
    }

//    public static void hideWindow() {
//        logger.debug("hide GameWindow");
//        if(App.win == null){
//            logger.debug("window not init");
//            return;
//        }
//        Stage stage = (Stage) App.win.getScene().getWindow();
//        stage.hide();
//    }

    public static String getWinner() {
        int maxPoints = -1;
        for (Player p: Game.getPlayers()) {
            if(p.getPoints() > maxPoints){
                maxPoints = p.getPoints();
            }
        }
        String winners = "";
        for (Player p: Game.getPlayers()) {
            if(p.getPoints() == maxPoints){
                winners += p.getDisplayName() + "\n";
            }
        }
        return winners;
    }

    public static String getResult() {
        String result = App.bundle.getString("playerPoints") + "\t \t"
                + App.bundle.getString("pointsPoints") + "\n";
        for (Player p: Game.getPlayers()) {
            result += p.getDisplayName() + "\t \t" + p.getPoints() + "\n";
        }
        return result;
    }
//=======================================================================================================================
    //******** CONNECTION METHODS ****************************************************************

    /**
     * Metoda zajistí inicializaci struktur pro síťovou komunikaci
     * @return informace o úsplěchu inicializace
     */
    static boolean connectServer() {
        logger.debug("Start method");

        // získání čísla portu
        int resultPort;
        try {
            resultPort = NetService.checkPort(controller.getPort());
        }catch (NumberFormatException e){
            logger.error("Port není číslo", e);
            resultPort = -1;
        }

        if(resultPort == -1){
            logger.error("Wrong range of port");

            Platform.runLater(() -> DialogFactory.alertError( bundle.getString("portErrorTitle")
                    , bundle.getString("portErrorHeadline")
                    , bundle.getString("portErrorText")));
            return false;
        }
        if (Thread.currentThread().isInterrupted()) {return false;} // nebyl jsem náhodou ukončen ?
        try{
            String text = bundle.getString("connectionTry");
            Platform.runLater(() -> {controller.setStatusText(text);});

            NetService.getInstance().setup(controller.getAddressTextField(), resultPort);
            NetService.getInstance().initialize();

            logger.trace("Iinit MessageHandler");
            NetService.initMessageHandler(new MessageHandler());
            logger.trace("start messageHandler");
            NetService.startMessageHandler();
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

    public static void connectCode(UserRecord value) {
        logger.debug("connectCode");
        controller.loggedForm();
        controller.startProgress();
        controller.setUpUser(value);

        boolean result = false;
        result = connectServer();    //pripoj server

        if(!result){  // když se nepovedlo připojení uvolni a skonči

            NetService.getInstance().destroy();
            controller.noLoggedForm();
            controller.stopProgress();
            return;
        }

        userRecord = value;
        Message msg = new Message(Event.COD, MessageType.login, value.getId().trim());
        NetService.getInstance().sender.addItem(msg);

    }

    public static void connectLogin() {
        boolean result = false;

        result = connectServer();    //pripoj server
        if(!result){  // když se nepovedlo připojení uvolni a skonči
            NetService.getInstance().destroy();
            controller.noLoggedForm();
            controller.stopProgress();
            return;
        }
        if (Thread.currentThread().isInterrupted()) {
            controller.noLoggedForm(); // když jsi přeušen vše uvolni a skonči
            controller.stopProgress();
            return;
        }
        sendLogin();
    }

    public static void reconnect(){
        logger.debug("start reconnect()");
        boolean stop = false;
        if(Player.getLocalPlayer() == null){
            logger.trace("není kam se opětovně připojit... nemám lokáního uživatele");
            stop = true;
        }
        if(NetService.isUserEnd()){ // if close come from user
            logger.debug("user invoke end of threads - no reconnect");
            stop = true;
        }
        if(stop){
            logger.fatal("Early end of reconnection");
            Platform.runLater(() -> App.controller.noLoggedForm());
            return;
        }
        reconnection++;
        Platform.runLater(App::closeGameWindow);
        Platform.runLater(controller::startProgress);
        Platform.runLater( () -> controller.setStatusText(bundle.getString("reconnect")));
        try {
            int remainingMS = Constants.RECONNECT_TIMEOUT_MS;
            Platform.runLater(() -> controller.setTime(Constants.RECONNECT_TIMEOUT_MS));
            while(remainingMS > 0) {
                sleep(1000);
                int tmpMs = remainingMS;
                Platform.runLater(() -> controller.setTime(tmpMs));
                remainingMS -= 1000;
            }
            Platform.runLater(() -> controller.setTime(0));
        } catch (InterruptedException e) {
            logger.info("interupted sleeping whilereconnect");
            logger.fatal("early end of reconnection");
            Platform.runLater(() -> App.controller.noLoggedForm());
            return;
        }
        UserRecord userRecord;
        try {
             userRecord = new UserRecord(Player.getLocalPlayer().getServerUid()
                    , Player.getLocalPlayer().getNick()
                    , NetService.getServerName()
                    , NetService.getPort());
            connectCode(userRecord);
        }catch (NullPointerException e){
            logger.error("problem with netservice");
            logger.fatal("early end of reconnection");
            Platform.runLater(() -> App.controller.noLoggedForm());
            return;
        }
        NetService.endOfReconnection();
    }


    public static void logout(){
        NetService.userEnding();
        if(App.loginWorker != null && App.loginWorker.isAlive()){
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

        controller.noLoggedForm();
        controller.setNoLoggedStatus();
        controller.stopProgress();
        NetService.getInstance().destroy();
        cleanGameStructures();
    }

    private static void cleanGameStructures() {
        Player.clean();
        Game.clean();
        closeGameWindow();
    }

    /**
     * Obnoví seznam her na serveru
     */
    public static void refreshGames() {
        controller.onRefresh();
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

    public static void addLoginWorker(Thread thread) {
        if(loginWorker == null){
            loginWorker = thread;
            loginWorker.start();
            return;
        }
        loginWorker.interrupt();
        loginWorker = thread;
        loginWorker.start();
    }

    public static void addXMLWorker(Thread thread) {
        if(xmlWorker == null){
            xmlWorker = thread;
            xmlWorker.start();
            return;
        }
        xmlWorker.interrupt();
        xmlWorker = thread;
        xmlWorker.start();
    }
}
