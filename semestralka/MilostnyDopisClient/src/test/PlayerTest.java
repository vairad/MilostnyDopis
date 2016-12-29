package test;


import game.Player;
import message.Event;
import message.Message;
import message.MessageType;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;


//==============================================================================================
//==============================================================================================


public class PlayerTest {

    public List<Player> players;

    @Before
    public void setUp() throws Exception {
        players = new LinkedList<>();
        players.add(new Player("Jedna"  , "uid1", 1));
        players.add(new Player("Dva"    , "uid2", 2));
        players.add(new Player("Tri"    , "uid3", 3));
        players.add(new Player("Ctyri"  , "uid4", 4));
        players.add(new Player("Pet"    , "uid5", 5));
        players.add(new Player("Sest"   , "uid6", 6));
        players.add(new Player("Sedm"   , "uid7", 7));

    }

    //================================================
    @Test
    public void testCompareTo1() {
        Collections.shuffle(players);
        Collections.sort(players);
        assertEquals("Chyba compareTo", 7, players.get(6).getOrder());
    }

    @Test
    public void testCompareTo2() {
        Collections.shuffle(players);
        Collections.sort(players);
        assertEquals("Chyba compareTo", 1, players.get(0).getOrder());
    }

}

