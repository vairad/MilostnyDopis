package gui;

import game.Game;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class GameRecord {
    private final boolean isRootNode;
    private final boolean started;
    private String uid;
    private int playersCount;
    private static List<GameRecord> allGameRecords;
    private boolean server;

    private GameRecord(String uid, int playersCount, boolean isRootNode){
        this.uid = uid;
        this.playersCount = playersCount;
        this.started = false;
        this.isRootNode = isRootNode;
    }

    public GameRecord(String gameS) {
        String[] parts = gameS.split("&&");
        this.uid = parts[0];
        this.playersCount = Integer.parseInt(parts[1]);
        this.started = Integer.parseInt(parts[2]) > 0;
        this.isRootNode = false;
    }

    public GameRecord(String serverName, boolean isServer) {
        this(serverName, 4, isServer); //todo fix stupid constructor
    }

    public static List<GameRecord> getAllGameRecords() {
        return allGameRecords;
    }

    public static void setAllGameRecords(List<GameRecord> allGameRecords) {
        GameRecord.allGameRecords = allGameRecords;
    }

    public static List<GameRecord> getAllConnectableGames() {
        List<GameRecord> playableGames = new LinkedList<>();
        for (GameRecord gr: allGameRecords) {
            if(!gr.isStarted()){
                playableGames.add(gr);
            }
        }
        return playableGames;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString(){
        if(isRootNode){
            return App.bundle.getString("server") + " " + uid;
        }
        return App.bundle.getString("game") + " " + uid + " (" + playersCount + ")";
    }

    public static GameRecord getGame(String msg) {
        try
        {
            for (GameRecord gr : allGameRecords ) {
                if(gr.getUid().equals(msg)){
                    return gr;
                }
            } 
        }catch (NullPointerException e){
            return null;
        }
        return null;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isServer() {
        return server;
    }
}
