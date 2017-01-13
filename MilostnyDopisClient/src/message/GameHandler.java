package message;

import game.Card;
import game.Game;
import game.GameStatus;
import game.Player;
import gui.App;
import gui.DialogFactory;
import gui.GameRecord;
import javafx.application.Platform;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by XXXXXXXXXXXXXXXX on 14.12.16.
 */
public class GameHandler {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(GameHandler.class.getName());


    public static void handleTypeGame(Message msg) {
        switch (msg.getEvent()){
            case ACK:
                handleGameACK(msg);
                break;
            case STA:
                handleGameSTA(msg);
                break;
            case CAR:
                handleGameCAR(msg);
                break;
            case NEP:
                handleGameNEP(msg);
                break;
            case TOK:
                handleGameTOK(msg);
                break;
            case PLA:
                handleGamePLA(msg);
                break;
            case RES:
                handleGameRES(msg);
                break;
            case PLS:
                handleGamePLS(msg);
                break;
            case PTS:
                handleGamePTS(msg);
                break;
            case NAK:
                handleGameNAK(msg);
                break;
            case UNK:
                logger.error("UNKNOWN GAME EVENT TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED GAME EVENT TYPE: " + msg.getEvent());
                break;
        }
    }

    private static void handleGameNAK(Message msg) {
        logger.debug("start method");
        Platform.runLater(DialogFactory::noGameDialog);
        Platform.runLater(App::refreshGames);
    }

    private static void handleGamePTS(Message msg) {
        logger.debug("stard method");
        String[] messageParts = msg.getMessage().split("&&");
        Card winnerCard = Card.getCardFromInt(Integer.parseInt(messageParts[0]));

        List<Player> winners = new LinkedList<>();
        for (int index = 1; index < messageParts.length; index++){
            Player player = Game.getPlayer(messageParts[index]);
            if(player != null){
                winners.add(player);
            }
        }
        Platform.runLater(() -> DialogFactory.roundEndDialog(winners, winnerCard ));
    }

    private static void handleGamePLS(Message msg) {
        String[] playersParts = msg.getMessage().split("@@");
        for (String playerRec : playersParts ) {
            handlePlayerPLS(playerRec);
        }
        Platform.runLater(() -> App.updatePlayers());
    }

    private static void handlePlayerPLS(String playerRec) {
        String[] attributes = playerRec.split("&&");

        boolean guarded = Boolean.parseBoolean(attributes[1]);
        boolean token = Boolean.parseBoolean(attributes[2]);
        boolean alive = Boolean.parseBoolean(attributes[3]);

        try {
            Game.getPlayer(attributes[0]).
                    setAttributes(alive, token, guarded);
        }catch (NullPointerException e){
            NetService.getInstance().sender.addItem(MessageFactory.getStatusMessage(Game.getUid()));
        }
    }

    private static void handleGameRES(Message msg) {
        logger.debug("start method");
        String[] messageParts = msg.getMessage().split("&&");
        if(messageParts.length > 2){
            Card playedCard = Card.getCardFromInt(Integer.parseInt(messageParts[0]));
            if(playedCard == Card.NONE){
                logger.error("nonsense card");
                return;
            }
            Player playerWhoPlays = Game.getPlayer(messageParts[1]);
            if(playerWhoPlays == null){
                logger.error("affect player out of game");
                return;
            }
            boolean isMyTarget = Boolean.parseBoolean(messageParts[2]);

            if(messageParts.length == 4){
                Platform.runLater(() -> DialogFactory.resultDialog(playedCard, playerWhoPlays, isMyTarget, messageParts[3]));
            }else {
                Platform.runLater(() -> DialogFactory.resultDialog(playedCard, playerWhoPlays, isMyTarget, null));
            }

        }
        logger.error("wrong format of res message " + messageParts.length);
    }

    private static void handleGamePLA(Message msg) {
        logger.debug("start");
        String[] messageParts = msg.getMessage().split("&&");
        if(messageParts.length < 2){
            logger.debug("špatný formát zprávy... málo částí");
            return;
        }
        Card receivedCard = null;
        Player player = null;
        if(messageParts[0].equals("CANCEL")){
            logger.trace("větev neuznané karty");

            switch (messageParts[2]) {
                case "WRONG":
                    receivedCard = Card.getCardFromInt(Integer.parseInt(messageParts[1]));
                    logger.debug("Vracím do ruky kartu :" + receivedCard);
                    Player.giveCard(receivedCard);
                    Platform.runLater(App::updateCards);
                    return;
                case "SAME":
                    receivedCard = Card.getCardFromInt(Integer.parseInt(messageParts[1]));
                    logger.debug("Vracím do ruky kartu :" + receivedCard);
                    Card tmpCard = receivedCard;
                    Platform.runLater(() -> DialogFactory.returnedCard(tmpCard));
                    Player.giveCard(receivedCard);
                    return;
                default:
                    receivedCard = Card.getCardFromInt(Integer.parseInt(messageParts[1]));
                    tmpCard = receivedCard;
                    Platform.runLater(() -> DialogFactory.wastedCard(tmpCard));
                    return;
            }
        }else{
            player = Game.getPlayer(messageParts[1]);
            try {
                receivedCard = Card.getCardFromInt(Integer.parseInt(messageParts[2]));
                logger.debug("Přijata zahraná karta: " + receivedCard);
            }catch (NumberFormatException e){
                logger.error("nesmysl ve zprávě: " + messageParts[2]);
                return;
            }
        }

        try {
            player.addCard(receivedCard);
        }catch (NullPointerException e){
            logger.error("Id hráče ve zprávě není v seznamu hráčů lokální hry. Nebo je karta nullová");
        }

        Platform.runLater(() -> App.addCard());

        // pokud jsem obdržel výsledek své karty... předávám token hry (pokud ho mám)
        try{
            if(player.equals(Player.getLocalPlayer())){
                if(Player.getLocalPlayer().haveToken()) {
                    giveTokenToServer();
                }
            }

        }catch (NullPointerException e){
            logger.error("Null pointer při předání tokenu");
            giveTokenToServer();
        }
    }

    private static void handleGameTOK(Message msg) {
        logger.debug("Dostal jsem token" + msg.getMessage());
        String[] messageParts = msg.getMessage().split("&&");
        if(messageParts.length > 2){
            logger.error("Zprava tokenu má moc částí");
            return;
        }
        if(messageParts.length == 1){
            if(messageParts[0].equals("OK")){
                logger.info("Server recieved token");
                return;
            }
        }

        if(!Game.getUid().equals(messageParts[0])){
            logger.debug("Špatné id hry a předávaného tokenu");
            return;
        }
        Game.clearToken();
        Player playerForToken = Game.getPlayer(messageParts[1]);
        if(playerForToken != null){
            playerForToken.giveToken();
            Platform.runLater(() -> App.moveTokenTo(playerForToken));
        }
        NetService.getInstance().sender.addItem(MessageFactory.getCards());
    }

    private static void handleGameCAR(Message msg) {
        logger.debug("Dostal jsem kartu:" + msg.getMessage() );
        try {
            Player.giveCard(Card.getCardFromInt(Integer.parseInt(msg.getMessage())));
        }catch (NumberFormatException e){
            String[] messageParts = msg.getMessage().split("&&");
            if(messageParts.length != 2){
                logger.trace("Nesmysl ve zprave o karte");
                return;
            }
            Player.resetCards(Card.getCardFromInt(Integer.parseInt(messageParts[0]))
                              ,Card.getCardFromInt(Integer.parseInt(messageParts[1])));
        }
        Platform.runLater(App::updateCards);
    }

    private static void handleGameNEP(Message msg) {
        if(App.win != null) {
            Message msgR = MessageFactory.getStatusMessage(Game.getUid());
            NetService.getInstance().sender.addItem(msgR);
        }
    }

    private static void handleGameSTA(Message msg) {
        if(Game.getUid().equals(msg.getMessage().substring(0,6))){
            logger.debug("game is over");
            Platform.runLater(() -> {
                DialogFactory.gameStandingsDialog();
                App.closeGameWindow();
            });
            return;
        }
        GameStatus status = new GameStatus(msg.getMessage());
        try {
            Game.reinitialize(status);
        }catch (NullPointerException e){
            logger.error("game is not initialized");
            GameRecord rec = GameRecord.getGame(status.getUid());
            if(rec == null){
                return;
            }
            Game.initialize(rec);
            Game.reinitialize(status);
            Platform.runLater(() -> App.highlightGame(rec));
        }
        if(Game.isReady()){
            Platform.runLater(App::showGameWindow);
        }
        NetService.getInstance().sender.addItem(MessageFactory.getCards());
    }

    private static void handleGameACK(Message msg) {
        logger.trace("Sart method");
        String[] parts = msg.getMessage().split("=");

        if(parts.length != 2){
            handleGameACKResponse(msg.getMessage());
        }else{
            handleGameACKList(parts);
        }

    }

    private static void handleGameACKList(String[] parts){
        logger.debug("Sart method");
        String[] gamesS = parts[1].split(";");
        List<GameRecord> gameRecords = new LinkedList<GameRecord>();
        for (String gameS : gamesS ) {
            try {
                gameRecords.add(new GameRecord(gameS));
            }catch (NumberFormatException e){
                logger.debug("Wrong format of players count in message");
            }
        }
        GameRecord.setAllGameRecords(gameRecords);
        Platform.runLater(App::smartFillTree);
    }

    private static void handleGameACKResponse(String msg){
        logger.debug("Sart method");

        GameRecord gameRecord = GameRecord.getGame(msg);
        if(gameRecord == null){
            logger.debug("Unknown game id returned from server");
            return;
        }
        try {
            if(!Game.getUid().equals(gameRecord.getUid())){
                Platform.runLater(() -> DialogFactory.differentGame(gameRecord));
            }
        }catch (NullPointerException e){
            logger.trace("null");
        }
        Platform.runLater(() -> App.newGame(gameRecord));
    }

    private static void giveTokenToServer(){
        Player.getLocalPlayer().takeToken();
        Message msg = new Message(Event.TOK, MessageType.game, Game.getUid());
        NetService.getInstance().sender.addItem(msg);
    }
}
