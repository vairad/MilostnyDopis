package gui;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.17.
 */
public class UserRecord {
    public static HashSet<UserRecord> allRecords;
    private final String id;
    private final String nick;
    private final String serverName;
    private final String port;

    public UserRecord(){
        id = "...";
        nick = "...";
        serverName = "...";
        port = "...";
    }

    public UserRecord(String id, String nick, String serverName, String port) {
        this.id = id;
        this.nick = nick;
        this.serverName = serverName;
        this.port = port;

        if(allRecords == null) allRecords = new LinkedHashSet<>();
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
        return nick + " (" + id + ") " + serverName + ":" + port;
    }

    public String getPort() {
        return port;
    }

    public String toXml() {
        return "<userRecord>"                    + "\n"
                +"<id>"+id+"</id>"              + "\n"
                + "<nick>"+nick+"</nick>"    + "\n"
                + "<server>"+serverName+"</server>"   + "\n"
                + "<port>"+port+"</port>"            + "\n"
                + "</userRecord>"                + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRecord that = (UserRecord) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getNick() != null ? !getNick().equals(that.getNick()) : that.getNick() != null) return false;
        if (getServerName() != null ? !getServerName().equals(that.getServerName()) : that.getServerName() != null)
            return false;
        return getPort() != null ? getPort().equals(that.getPort()) : that.getPort() == null;

    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
