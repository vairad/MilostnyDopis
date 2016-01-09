package net;

import java.util.logging.Logger;
import java.util.logging.LogManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.16.
 */
public class Sender implements Runnable {

    public static Logger logger; // inicializace probíhá v konfiguraci logovani

    private BlockingQueue<String> toSend;
    private int port;
    private InetAddress address;
    private NetService netservice;

    public Sender(int port, InetAddress address, BlockingQueue<String> toSend, NetService netservice){
        this.netservice = netservice;
        this.toSend = toSend;
        this.port = port;
        this.address = address;
    }

    @Override
    public void run(){
        while(true){
            try {
                send(toSend.take());
            } catch (InterruptedException e) {
                logger.severe("Thread was interupted");
               // logger.error(e);
            }
        }
    }

    private void send(String msg){
        byte buffer[] = msg.getBytes(); // připrav data do bufferu

        logger.info("Data k odeslani = " + msg + "\n");
        DatagramPacket send;
        send = new DatagramPacket(buffer, buffer.length, address, port);
        try {
            netservice.getDatagramSocket().send(send);
            logger.info("Data odeslana.");
        }catch (IOException e){
            logger.severe("Socket problem");
            //logger.error(e);
        }
    }

}
