package game;

import gui.GameRecord;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class Game {
    private final GameRecord game;
    private List<User> players;
    private static List<Game> allGameRecords;

    public Game(GameRecord game){
        players = new LinkedList<User>();
        this.game = game;
    }

    public String getUid() {
        return game.getUid();
    }

}
