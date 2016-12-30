package game;

import constants.PlayerPosition;

/**
 * Created by XXXXXXXXXXXXXXXX on 16.11.16.
 */
public class Player implements Comparable<Player> {
    private static Player local_player = null;
    private static String localUid = null;
    private final int order;
    private PlayerPosition display_order;

    private Card myCard = Card.NONE;
    private Card secondCard = Card.NONE;

    private boolean local;

    /** Player properties */
    private String nick;
    private String serverUid;

    /** State properties */
    private boolean logged;
    private boolean saved;

    public Player(String nick, String serverUid, int order){
        this.nick = nick;
        this.serverUid = serverUid;
        this.order = order;
        if(serverUid.equals(localUid)){
            this.local = true;
        }
    }

    public Player(String nick, String serverUid) {
        this(nick, serverUid, -1);
    }


    public static Player getLocalPlayer() {
        return local_player;
    }

    public static void setLocalPlayer(Player local_player) {
        Player.localUid = local_player.serverUid;
        Player.local_player = local_player;
    }

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

    public boolean isLogged() {
        return logged;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isLocal() {
        return local;
    }

    public int getOrder() {
        return order;
    }

    public PlayerPosition getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(PlayerPosition display_order) {
        this.display_order = display_order;
    }

    @Override
    public int compareTo(Player o) {
        return order - o.order;
    }

    public static void giveCard(Card cardFromInt) {
        if (local_player.myCard == Card.NONE){
            local_player.myCard = cardFromInt;
            return;
        }
        local_player.secondCard = cardFromInt;
    }

    public Card getSecondCard() {
        return secondCard;
    }

    public Card getMyCard() {
        return myCard;
    }
}
