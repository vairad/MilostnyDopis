package game;

import gui.GameRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class Game {
    private static GameRecord game;
    private static List<Player> players;
    private static List<Game> allGameRecords;

    public static void initialize(GameRecord game){
        players = new LinkedList<Player>();
        game = game;
    }

    public String getUid() {
        return game.getUid();
    }

}
