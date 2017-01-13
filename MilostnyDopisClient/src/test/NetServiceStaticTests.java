package test;

import netservice.NetService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


//==============================================================================================
//==============================================================================================


public class NetServiceStaticTests {

    @Before
    public void setUp() throws Exception {
    }

    //==========TESTOVANI STATICKYCH METOD TRIDY NETSERVICE======================================


    @Test
    public void testPortLowBound1() {
        int result = NetService.checkPort("1");

        assertEquals("Chybně NEakceptována 1", 1, result);
    }

    @Test
    public void testPortLowBound2() {
        int result = NetService.checkPort("0");

        assertEquals("Chybně akceptována 0", -1, result);
    }

    @Test
    public void testPortHighBound1() {
        int result = NetService.checkPort("65535");

        assertEquals("Chybně NEakceptováno 65535", 65535, result);
    }

    @Test
    public void testPortHighBound2() {
        int result = NetService.checkPort("65536");

        assertEquals("Chybně akceptováno 65536", -1, result);
    }

    @Test
    public void testPortInBounds1() {
        int result = NetService.checkPort("25");

        assertEquals("Chybně NEakceptováno 25", 25, result);
    }

    @Test
    public void testPortInBounds2() {
        int result = NetService.checkPort("2127");

        assertEquals("Chybně NEakceptováno 2127", 2127, result);
    }

    @Test
    public void testPortOutBounds1() {
        int result = NetService.checkPort("-26");

        assertEquals("Chybně akceptováno -26", -1, result);
    }

    @Test
    public void testPortOutBounds2() {
        int result = NetService.checkPort("75568");

        assertEquals("Chybně akceptováno 75568", -1, result);
    }
}

