package game;

/**
 * Created by XXXXXXXXXXXXXXXX on 16.11.16.
 */
public class User {
    private static final User INSTANCE = new User();

    public static User getInstance() {
        return INSTANCE;
    }

    private User() {
        nick = new String();
        serverUid = new String();
    }

    private String nick;
    private String serverUid;
    private boolean logged;
    private boolean saved;


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        this.saved = false;
    }

    public String getServerUid() {
        return serverUid;

    }

    public void setServerUid(String serverUid) {
        this.serverUid = serverUid;
        this.saved = false;
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }
}
