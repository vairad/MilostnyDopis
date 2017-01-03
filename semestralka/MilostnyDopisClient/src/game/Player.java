package game;

import constants.PlayerPosition;

import java.util.LinkedList;
import java.util.List;

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
    private List<Card> playedCards;
    private boolean alive;
    private boolean token;

    public Player(String nick, String serverUid, int order, boolean alive, boolean token){
        this.nick = nick;
        this.serverUid = serverUid;
        this.order = order;
        this.alive = alive;
        if(serverUid.equals(localUid)){
            this.local = true;
        }
        playedCards = new LinkedList<>();
        this.token = token;
    }

    public Player(String nick, String serverUid) {
        this(nick, serverUid, -1, false, false);
    }



    public static Player getLocalPlayer() {
        return local_player;
    }

    public static void setLocalPlayer(Player local_player) {
        local_player.setDisplay_order(PlayerPosition.LOCAL);
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

    public String getDisplayName() {
        return nick + " (" +serverUid +")";
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public boolean isAlive() {
        return alive;
    }

    /**
     * Dvě instance Player jsou stejné pokud odkazují na shodné uid hráče
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if(o instanceof Player )
            return ((Player) o).serverUid.equals(serverUid);
        return false;
    }

    public void resetCards(List<Card> cardList) {
        playedCards.clear();
        playedCards.addAll(cardList);
    }

    public void giveToken() {
        token = true;
    }

    public void takeToken(){
        token = false;
    }

    public boolean haveToken(){
        return token;
    }
}
