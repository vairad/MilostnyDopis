package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import net.MessageFactory;
import org.junit.Before;
import org.junit.Test;


//==============================================================================================
//==============================================================================================


public class MessageFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    //==========TESTOVANI SRV ZPRAV========================================================================================
    @Test
    public void testServerMessageList() {
        String pattern = "SRV#024#nnnnnnnnn#list##";

       String msg = MessageFactory.serverMessage("list");

        assertEquals("Chybne vytvořeni zprávy SRV-list", pattern, msg);
    }

    @Test
    public void testServerMessageList1() {
        String pattern = "SRV#023#nnnnnnnnn#new##";

        String msg = MessageFactory.serverMessage("new");

        assertEquals("Chybne vytvořeni zprávy SRV-new", pattern, msg);
    }

    @Test
    public void testServerMessageList2() {
        String pattern = "SRV#037#nnnnnnnnn#connect#ggggggggg##";

        String msg = MessageFactory.serverMessage("connect#ggggggggg");

        assertEquals("Chybne vytvořeni zprávy SRV-connect", pattern, msg);
    }






}

