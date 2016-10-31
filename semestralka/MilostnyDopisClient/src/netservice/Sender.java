package netservice;

import message.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public class Sender extends Thread {
    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Sender.class.getName());

    private BlockingQueue<Message> toSend = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while(NetService.runFlag){
            Message msg = null;
            try {
                msg = toSend.take();
            } catch (InterruptedException e) {
                logger.error("Chyba při práci s kolekcí." , e);
            }

            if(msg == null){
                logger.error("Vybrán prázdý element z kolekce");
                continue;
            }

            OutputStream out = NetService.getInstance().getOutputStream();
            if(out == null){
                logger.error("NUll out stream ... The end of sending");
                return;
            }

            String messageToNet = msg.toString();

            if(messageToNet == null){
                logger.error("Null message to send");
                continue;
            }

            try {
                out.write(messageToNet.getBytes());
            } catch (IOException e) {
                logger.error("IO Error ... sending is over");
                return;
            }


        }

    }

    public synchronized boolean addItem(Message msg){
        return toSend.add(msg);
    }
}
