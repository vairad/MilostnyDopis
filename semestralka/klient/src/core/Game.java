package core;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents instance of game wth all mechanisms.
 * Created by Radek VAIS
 */
public class Game {

    private Deck deck;

    private Player user;
    private Player p1;
    private Player p2;
    private Player p3;


    /**
     * Prepare ga game for two players.
     * @param user player on this station
     * @param p2 player on remote station
     */
    public Game(Player user, Player p2){
        this(user, p2, null);
    }


    /**
     * Prepare ga game for three players.
     * @param user player on this station
     * @param p2 player on remote station
     * @param p3 player on remote station
     */
    public Game(Player user, Player p2, Player p3){
        this(user, p2, p3, null);
    }

    /**
     * Prepare ga game for four players.
     * @param user player on this station
     * @param p2 player on remote station
     * @param p3 player on remote station
     * @param p4 player on remote station
     */
    public Game(Player user, Player p2, Player p3, Player p4) {

    }

    /**
     * Method prepare deck of cards for game in random order.
     * @return random sorted deck of cards
     */
    private Deck prepare_deck(){
        return new Deck();
    }

}
