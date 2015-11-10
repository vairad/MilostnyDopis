package core;

import java.util.LinkedList;
import java.util.List;

/**
 * Class represents player of the game.
 * Created by Radek VAIS
 * Version: xxxxxxxxxxxxxxxxxxxxxxxxx
 */
public class Player {

    /** in game id of player */
    private int player_id;
    /** list of played cards */
    private List<Card> played_cards;
    /** actual card in hand */
    private Card actual_card;


    /**
     * Create player for first game. It means that his hand is empty and he haven't play any card yet.
     * played_cards set to empty list.
     * actual_card is set to null.
     * @param player_id int id of player in range from 1 to 4
     */
    public Player(int player_id) {
        this.player_id = player_id;
        this.played_cards = new LinkedList<Card>();
        this.actual_card = null;
    }


//############################ GETTERS ###############################################

    /**
     * Returns number of player. in range from 1 to 4.
     * @return int number of player
     */
    public int getPlayer_id() {
        return player_id;
    }

    /**
     * Returns list of used cards lying on the bord in front of player.
     * @return list of used cards by this player
     */
    public List<Card> getPlayed_cards() {
        return played_cards;
    }

    /**
     * Returns card which player holds in his hand.
     * @return card in players hand
     */
    public Card getActual_card() {
        return actual_card;
    }
}
