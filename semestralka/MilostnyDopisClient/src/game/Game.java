package game;

import gui.App;
import gui.GameRecord;
import javafx.application.Platform;
import message.Event;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class Game {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(Game.class.getName());

    private static GameRecord gameRecord;
    private static List<Player> players;
    private static List<Game> allGameRecords;
    private static boolean ready;

    public static void initialize(GameRecord gameRecord){
        players = new LinkedList<Player>();
        Game.gameRecord = gameRecord;
        ready = false;
    }

    public static String getUid() {
        return gameRecord.getUid();
    }

    public static void reinitialize(GameStatus gameStatus) {
        if(!gameStatus.getUid().equals(gameRecord.getUid())){
            logger.error("Nesouhlasi id stavu hry a hry inicializované");
            return;
        }

        setUpPlayers(gameStatus.players);

        ready = true;
    }

    private static void setUpPlayers(List<Player> players) {
        Game.players.clear();
        Game.players = players;
        Platform.runLater(App::setUpPlayers);
    }

    public static boolean isReady() {
        return ready;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Message getStatusMessage() {
        return new Message(Event.STA, MessageType.game, getUid());
    }

    public static Player getPlayer(String playerUid) {
        for (Player p : players ) {
            if(p.getServerUid().equals(playerUid)){
                if(p.equals(Player.getLocalPlayer())){
                    return Player.getLocalPlayer();
                } else {
                    return p;
                }
            }
        }
        return null;
    }
}
