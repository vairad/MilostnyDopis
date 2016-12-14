package netservice;

import message.Event;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public class Reciever extends Thread {
    private static final int MSG_HEADER_LEN = 14;

    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Reciever.class.getName());

    private byte[] buffer;

    @Override
    public void run() {
        while (NetService.runFlag){
            buffer = new byte[NetService.MAX_MSG_LENGTH];
            InputStream in = NetService.getInstance().getInputStream();

            if(in == null){
                logger.error("in stream is null");
                return;
            }
            int recvBytes = 0;
            try {
                recvBytes = in.read(buffer, 0, NetService.MAX_MSG_LENGTH);
            } catch (IOException e) {
                logger.error("IO ERROR", e);
                return;
            }
            if (recvBytes == -1){
                logger.debug("IO disconnect");
                return;
            }

            String msgS = new String(buffer);
            logger.debug(msgS);

            Message msg = new Message(msgS);

            boolean result = checkMessage(msg);
            if(!result){
                logger.error("Chyba zpracování zprávy: " + msg);
            }

            result = addItem(msg);
            if(!result){
                logger.error("Chyba vložení do fronty zprávy: " + msg);
            }

            logger.debug("Dostal jsem zpravu" + msg);
      }
    }

    private boolean checkMessage(Message msg) {
        if(msg.getType() == null || msg.getType() == MessageType.unknown){
            return false;
        }
        if(msg.getEvent() == null || msg.getEvent() == Event.UNK){
            return false;
        }
        return true;
    }

    private boolean addItem(Message msg){
        return NetService.getInstance().toServe.add(msg);
    }
}
