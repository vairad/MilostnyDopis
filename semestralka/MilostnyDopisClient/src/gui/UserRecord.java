package gui;

import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.17.
 */
public class UserRecord {
    public static List<UserRecord> allRecords;
    private final String id;
    private final String nick;
    private final String serverName;

//    public UserRecord(){
//        id = "...";
//        nick = "...";
//        serverName = "...";
//    }

    public UserRecord(String id, String nick, String serverName) {
        this.id = id;
        this.nick = nick;
        this.serverName = serverName;
    }

    public String getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getServerName() {
        return serverName;
    }

    public String toString(){
        return nick + " (" + id + ") " + serverName;
    }
}
