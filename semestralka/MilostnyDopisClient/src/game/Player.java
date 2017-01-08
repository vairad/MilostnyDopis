package game;

import constants.PlayerPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by XXXXXXXXXXXXXXXX on 16.11.16.
 */
public class Player implements Comparable<Player> {
    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Player.class.getName());

    private static Player local_player = null;
    private static String localUid = null;

    /** Player properties */
    private String nick;
    private String serverUid;

    private Card myCard;
    private Card secondCard;

    private final int order;
    private PlayerPosition display_order;

    private boolean local;

    /** State properties */
    private boolean logged;
    private boolean saved;
    private List<Card> playedCards;
    private boolean alive;
    private boolean token;
    private boolean guarded;

    private Semaphore collection_lock;

    public Player(String nick, String serverUid, int order, boolean alive, boolean token, boolean guarded){
        this.nick = nick;
        this.serverUid = serverUid;
        this.order = order;
        this.alive = alive;
        this.guarded = guarded;
        if(serverUid.equals(localUid)){
            this.local = true;
        }
        playedCards = new LinkedList<>();
        this.token = token;

        myCard = Card.NONE;
        secondCard = Card.NONE;
        collection_lock = new Semaphore(1);
    }

    public void acquireCollection(){
        try {
            logger.debug("get mutex player collection");
            collection_lock.acquire();
        } catch (InterruptedException e) {
            logger.trace("interrupted");
        }
    }

    public void releaseCollection(){
        logger.debug("give mutex player collection");
        collection_lock.release();
    }


    public  Player(String nick, String serverUid, int order){
        this(nick, serverUid, order, false, false, false);
    }

    public Player(String nick, String serverUid) {
        this(nick, serverUid, -1, false, false, false);
    }

    public static Player getLocalPlayer() {
        return local_player;
    }



    /**
     * Lokální hráč bude nastaven, pokud je null.
     * Pokud není null, převezmou se servrem hlavní data o ráči ze serveru, zbytek se ponechá lokání.
     * @param local_player nastavovaný hráč
     */
    public static void  setLocalPlayer(Player local_player) {
        if(local_player.equals(Player.getLocalPlayer())){
            //todo porovnej informace serveru a lokálního hráče
            return;
        }
        local_player.setDisplay_order(PlayerPosition.LOCAL);
        Player.localUid = local_player.serverUid;
        Player.local_player = local_player;
        Player.local_player.local = true;
    }

    public String getNick() {
        return nick;
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

    /**
     * Give card to local players hand
     * @param cardFromInt
     */
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
        acquireCollection();
        playedCards.clear();
        playedCards.addAll(cardList);
        releaseCollection();
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

    public boolean isGuarded() {
        return guarded;
    }

    /**
     * Add card to collection of played cards
     * @<code>playedCards</code>
     * @param card
     */
    public void addCard(Card card) {
        acquireCollection();
        playedCards.add(card);
        releaseCollection();
    }

    public void playMyCard() {
        myCard = secondCard;
        secondCard = Card.NONE;
    }

    public void playSecondCard() {
        secondCard = Card.NONE;
    }

    public static void resetCards(Card myCard, Card secondCard) {
        local_player.myCard = myCard;
        local_player.secondCard = secondCard;
    }

    public static void updateLocalPlayer(Player player) {
        local_player.acquireCollection();
        local_player.setAttributes(player.alive, player.token, player.guarded);
        local_player.playedCards.clear();
        local_player.playedCards.addAll(player.getPlayedCards());
        local_player.releaseCollection();
    }

    public void setAttributes(boolean alive, boolean token, boolean guarded) {
        this.alive = alive;
        this.token = token;
        this.guarded = guarded;
    }

}
