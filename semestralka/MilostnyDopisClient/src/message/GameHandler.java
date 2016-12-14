package message;

import gui.GameRecord;
import gui.App;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

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
            case UNK:
                logger.error("UNKNOWN LOGIN EVENT TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED LOGIN EVENT TYPE");
                break;
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
        Platform.runLater(() -> App.fillTree(gameRecords));
    }

    private static void handleGameACKResponse(String msg){
        logger.debug("Sart method");

    }
}
