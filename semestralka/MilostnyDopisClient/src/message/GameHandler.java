package message;

import game.Card;
import game.Game;
import game.GameStatus;
import game.Player;
import gui.GameRecord;
import gui.App;
import javafx.application.Platform;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.resources.Messages_sv;

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
            case UNK:
                logger.error("UNKNOWN GAME EVENT TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED GAME EVENT TYPE: " + msg.getEvent());
                break;
        }
    }

    private static void handleGameCAR(Message msg) {
        logger.debug("Dostal jsem kartu:" + msg.getMessage() );
        while(App.win == null){
            try {
                sleep(500);
            } catch (InterruptedException e) {
               logger.debug("Přerušeno čekání na vytvoření okna");
            }
        }
        App.win.addStatusMessage("Dostal jsem kartu:" + msg.getMessage()); // todo handle null pointer
        try {
            Player.giveCard(Card.getCardFromInt(Integer.parseInt(msg.getMessage())));
        }catch (NumberFormatException e){
            logger.trace("Nesmysl ve zprave o karte");
        }
        Platform.runLater(App::showMessagesGameWindow);
        Platform.runLater(App::updateCards);
    }

    private static void handleGameNEP(Message msg) {
        if(App.win != null) {
            App.win.addStatusMessage(msg.getMessage());
            Platform.runLater(App::showMessagesGameWindow);
            Message msgR = Game.getStatusMessage();
            NetService.getInstance().sender.addItem(msgR);
        }
    }

    private static void handleGameSTA(Message msg) {
        Game.reinitialize(new GameStatus(msg.getMessage()));
        if(Game.isReady()){
            Platform.runLater(App::showGameWindow);
        }
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
        Platform.runLater(() -> {
            App.fillTree(gameRecords);
        });
    }

    private static void handleGameACKResponse(String msg){
        logger.debug("Sart method");

        GameRecord gameRecord = GameRecord.getGame(msg);
        if(gameRecord == null){
            logger.debug("Unknown game id returned from server");
            return;
        }

        //todo check correct ID of recieved item
    }
}
