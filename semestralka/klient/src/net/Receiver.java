package net;

import java.util.logging.Logger;
import java.util.logging.LogManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.16.
 */
public class Receiver implements Runnable {

    public static Logger logger; // inicializace probíhá v konfiguraci logovani

    private NetService netService;
    private BlockingQueue<String> recvMsgs;

    public Receiver(BlockingQueue<String> recvMsgs, NetService netservice){
        this.netService = netservice;
        this.recvMsgs = recvMsgs;
    }

    @Override
    public void run(){
        byte[] buffer = new byte[512];
        DatagramPacket recv = new DatagramPacket(buffer, buffer.length );
        String data;

        while(true) {
            try {
                netService.getDatagramSocket().receive(recv);       //poslouchej na soketu

                data = new String(buffer);                          // prectena data jsou retezec
                logger.info("Prijal sem data - " + data + "\n");
                recvMsgs.put(data);                                 // pridej je do fronty ke zpracivani
            } catch (IOException e) {               // chyba na soketu
                logger.severe("Socket exception");
               // logger.error(e);
            } catch (InterruptedException e) {      //chyba ve frontě
                logger.severe("Queue exception");
               // logger.error(e);
            }
        }
    }
}
