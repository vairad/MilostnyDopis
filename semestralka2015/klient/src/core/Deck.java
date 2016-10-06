package core;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents deck of cards for one round of game
 * Created by Radek VAIS
 */
public class Deck {

    private List<ICard> deck;

    public Deck(){
        deck = new LinkedList<ICard>();

    }

}
