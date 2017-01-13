package netservice;

import message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
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
        while(NetService.isRunning()){
            Message msg = null;
            if(Thread.currentThread().isInterrupted()){
                return;
            }
            try {
                msg = toSend.take();
            } catch (InterruptedException e) {
                logger.error("Čekání bylo přerušeno." , e);
                return;
            }

            if(msg == null){
                logger.error("Vybrán prázdý element z kolekce");
                continue;
            }

            OutputStream out = NetService.getInstance().getOutputStream();
            if(out == null){
                logger.error("NUll out stream ... Trying to reconnect");
                new Thread(NetService::reconnect).start();
                return;
            }

            String messageToNet = msg.toString();
            logger.debug("Sending: " + msg.toString());

            if(messageToNet == null){
                logger.error("Null message to send");
                continue;
            }

            try {
                out.write(messageToNet.getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                logger.error("IO Error ... sending is over ... trying to reconnect");
                new Thread(NetService::reconnect).start();
                return;
            }
            NetService.sendBytes += messageToNet.getBytes().length;
        }
    }

    public synchronized boolean addItem(Message msg){
        return toSend.add(msg);
    }
}
