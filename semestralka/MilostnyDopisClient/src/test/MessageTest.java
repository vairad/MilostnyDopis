package test;

import com.sun.xml.internal.ws.api.model.MEP;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


//==============================================================================================
//==============================================================================================


public class MessageTest {

    @Before
    public void setUp() throws Exception {
    }

    //==========TESTOVANI STATICKYCH METOD TRIDY NETSERVICE======================================
    @Test
    public void testMessageType1() {
        Message msg = new Message(Event.ECH, MessageType.message, "Ahoj");

        assertEquals("Chybně vytvořená zpráva MSG ECH"
                , "###0018MSGECH#Ahoj"
                , msg.toString());
    }

    @Test
    public void testMessageType2() {
        Message msg = new Message(Event.ECH, MessageType.servis, "Ahoj");

        assertEquals("Chybně vytvořená zpráva SER ECH"
                , "###0018SERECH#Ahoj"
                , msg.toString());
    }
    @Test
    public void testMessageType3() {
        Message msg = new Message(Event.ECH, MessageType.game, "Ahoj");

        assertEquals("Chybně vytvořená zpráva GAM ECH"
                , "###0018GAMECH#Ahoj"
                , msg.toString());
    }
    @Test
    public void testMessageType4() {
        Message msg = new Message(Event.ECH, MessageType.login, "Ahoj");

        assertEquals("Chybně vytvořená zpráva LOG ECH"
                , "###0018LOGECH#Ahoj"
                , msg.toString());
    }
    @Test
    public void testMessageType5() {
        Message msg = new Message(Event.ECH, MessageType.unknown, "Ahoj");

        assertEquals("Chybně vytvořená zpráva UNK ECH"
                , "###0018UNKECH#Ahoj"
                , msg.toString());
    }



    @Test
    public void testMessageEvent1() {
        Message msg = new Message(Event.ECH, MessageType.unknown, "Ahoj");

        assertEquals("Chybně vytvořená zpráva UNK ECH"
                , "###0018UNKECH#Ahoj"
                , msg.toString());
    }

    @Test
    public void testMessageEvent2() {
        Message msg = new Message(Event.ACK, MessageType.unknown, "Ahoj");

        assertEquals("Chybně vytvořená zpráva UNK ACK"
                , "###0018UNKACK#Ahoj"
                , msg.toString());
    }

    @Test
    public void testMessageEvent3() {
        Message msg = new Message(Event.NAK, MessageType.unknown, "Ahoj");

        assertEquals("Chybně vytvořená zpráva UNK NAK"
                , "###0018UNKNAK#Ahoj"
                , msg.toString());
    }

    @Test
    public void testMessageEvent4() {
        Message msg = new Message(Event.UNK, MessageType.unknown, "Ahoj");

        assertEquals("Chybně vytvořená zpráva UNK UNK"
                , "###0018UNKUNK#Ahoj"
                , msg.toString());
    }
}

