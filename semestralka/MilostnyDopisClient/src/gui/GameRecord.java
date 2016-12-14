package gui;

import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.12.16.
 */
public class GameRecord {
    private final boolean isRootNode;
    private String uid;
    private int playersCount;
    private static List<GameRecord> allGameRecords;

    public GameRecord(String uid, int playersCount, boolean isRootNode){
        this.uid = uid;
        this.playersCount = playersCount;
        this.isRootNode = isRootNode;
    }

    public GameRecord(String gameS) {
        String[] parts = gameS.split("&&");
        this.uid = parts[0];
        this.playersCount = Integer.parseInt(parts[1]);
        this.isRootNode = false;
    }

    public GameRecord(String serverName, boolean isServer) {
        this(serverName, 4, isServer);
    }

    public static List<GameRecord> getAllGameRecords() {
        return allGameRecords;
    }

    public static void setAllGameRecords(List<GameRecord> allGameRecords) {
        GameRecord.allGameRecords = allGameRecords;
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

}
