package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import netservice.NetService;
import org.junit.Before;
import org.junit.Test;


//==============================================================================================
//==============================================================================================


public class NetServiceStaticTests {

    @Before
    public void setUp() throws Exception {
    }

    //==========TESTOVANI STATICKYCH METOD TRIDY NETSERVICE======================================
    @Test
    public void testIpLowBound1() {
        boolean result = NetService.checkIpOctet("1");

        assertEquals("Chybně NEakceptována 1", true, result);
    }

    @Test
    public void testIpLowBound2() {
        boolean result = NetService.checkIpOctet("0");

        assertEquals("Chybně akceptována 0", false, result);
    }

    @Test
    public void testIpHighBound1() {
        boolean result = NetService.checkIpOctet("255");

        assertEquals("Chybně NEakceptováno 255", true, result);
    }

    @Test
    public void testIpHighBound2() {
        boolean result = NetService.checkIpOctet("256");

        assertEquals("Chybně akceptováno 256", false, result);
    }

    @Test
    public void testIpInBounds1() {
        boolean result = NetService.checkIpOctet("25");

        assertEquals("Chybně NEakceptováno 25", true, result);
    }

    @Test
    public void testIpInBounds2() {
        boolean result = NetService.checkIpOctet("127");

        assertEquals("Chybně NEakceptováno 127", true, result);
    }

    @Test
    public void testIpOutBounds1() {
        boolean result = NetService.checkIpOctet("-26");

        assertEquals("Chybně akceptováno -26", false, result);
    }

    @Test
    public void testIpOutBounds2() {
        boolean result = NetService.checkIpOctet("2568");

        assertEquals("Chybně NEakceptováno 2568", false, result);
    }


    @Test
    public void testPortLowBound1() {
        boolean result = NetService.checkPort("1");

        assertEquals("Chybně NEakceptována 1", true, result);
    }

    @Test
    public void testPortLowBound2() {
        boolean result = NetService.checkPort("0");

        assertEquals("Chybně akceptována 0", false, result);
    }

    @Test
    public void testPortHighBound1() {
        boolean result = NetService.checkPort("65535");

        assertEquals("Chybně NEakceptováno 65535", true, result);
    }

    @Test
    public void testPortHighBound2() {
        boolean result = NetService.checkPort("65536");

        assertEquals("Chybně akceptováno 65536", false, result);
    }

    @Test
    public void testPortInBounds1() {
        boolean result = NetService.checkPort("25");

        assertEquals("Chybně NEakceptováno 25", true, result);
    }

    @Test
    public void testPortInBounds2() {
        boolean result = NetService.checkPort("2127");

        assertEquals("Chybně NEakceptováno 2127", true, result);
    }

    @Test
    public void testPortOutBounds1() {
        boolean result = NetService.checkPort("-26");

        assertEquals("Chybně akceptováno -26", false, result);
    }

    @Test
    public void testPortOutBounds2() {
        boolean result = NetService.checkPort("75568");

        assertEquals("Chybně NEakceptováno 75568", false, result);
    }
}

