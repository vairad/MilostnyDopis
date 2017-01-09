package game;

import gui.App;
import gui.GameRecord;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
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
    private static boolean ready;
    private static Long lastStatusSeq;

    public static void initialize(GameRecord gameRecord){
        players = new LinkedList<>();
        Game.gameRecord = gameRecord;
        ready = false;
        lastStatusSeq = (long) 0;
    }

    public static String getUid() {
        return gameRecord.getUid();
    }

    public static void reinitialize(GameStatus gameStatus) {
        if(!gameStatus.getUid().equals(gameRecord.getUid())){
            logger.error("Nesouhlasi id stavu hry a hry inicializované");
            return;
        }
        if(lastStatusSeq > gameStatus.getSeqNumber()){
            logger.debug("ignorovan stary zaznam o hre");
            return;
        }

        setUpPlayers(gameStatus.players);
        Platform.runLater(() -> {
            try{
                App.win.setTitle(Game.getUid());
            }catch (Exception e){
                logger.error("Něco špatného sestalo ve státě dánském");
            }
        });

        ready = true;
    }

    private static void setUpPlayers(List<Player> players) {
        Game.players.clear();
        Game.players = players;
        Player.resetCards(Card.NONE, Card.NONE);
        Platform.runLater(App::setUpPlayers);
        Platform.runLater(App::updateCards);
    }

    public static boolean isReady() {
        return ready;
    }

    public static List<Player> getPlayers() {
        return players;
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

    public static void clearToken() {
        Player.getLocalPlayer().takeToken();
        players.forEach(Player::takeToken);
    }
}
