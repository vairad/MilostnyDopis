package game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class Game {
    private final boolean isRootNode;
    private String uid;
    private List<User> players;
    private int playersCount;
    private static List<Game> allGames;

    public Game(String uid, int playersCount, boolean isRootNode){
        players = new LinkedList<User>();
        this.uid = uid;
        this.playersCount = playersCount;
        this.isRootNode = isRootNode;
    }

    public Game(String gameS) {
        String[] parts = gameS.split("&&");
        this.uid = parts[0];
        this.playersCount = Integer.parseInt(parts[1]);
        this.isRootNode = false;
    }

    public Game(String serverName, boolean isServer) {
        this(serverName, 4, isServer);
    }

    public static List<Game> getAllGames() {
        return allGames;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString(){
        if(isRootNode){
            return "Server " + uid;
        }
        return "Hra: " + uid + " (" + playersCount + ")";
    }
}
