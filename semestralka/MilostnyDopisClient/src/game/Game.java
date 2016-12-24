package game;

import gui.GameRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class Game {
    private static GameRecord gameRecord;
    private static List<Player> players;
    private static List<Game> allGameRecords;
    private static boolean ready;

    public static void initialize(GameRecord gameRecord){ //todo rename intialize methods
        players = new LinkedList<Player>();
        Game.gameRecord = gameRecord;
        ready = true;
    }

    public static String getUid() {
        return gameRecord.getUid();
    }

    public static void initialize(GameStatus gameStatus) {

    }

    public static boolean isReady() {
        return ready;
    }
}
