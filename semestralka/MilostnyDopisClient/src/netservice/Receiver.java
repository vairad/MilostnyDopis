package netservice;

import gui.App;
import message.Event;
import message.FormatException;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public class Receiver extends Thread {
    private static final int MSG_HEADER_LEN = 14;

    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Receiver.class.getName());

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
            }catch (SocketTimeoutException e){
                continue;
            }
            catch (IOException e) {
                logger.error("IO ERROR", e);
                new Thread(App::reconnect).start();
                return;
            }
            if (recvBytes == -1){
                logger.debug("IO disconnect");
                new Thread(App::reconnect).start();
                return;
            }

            NetService.recvBytes += recvBytes;

            String msgS = new String(buffer);
            logger.trace(msgS);

            for (String subMessage : msgS.split("###")) {
                Message msg;
                try {
                    msg = new Message("###" + subMessage);
                }catch (FormatException e){
                    logger.trace("too short message: ###" + subMessage);
                    continue;
                }
                boolean result = checkMessage(msg);
                if(!result){
                    logger.error("Chyba zpracování zprávy: " + msg);
                }

                result = addItem(msg);
                if(!result){
                    logger.error("Chyba vložení do fronty zprávy: " + msg);
                }

                logger.debug("Received message: " + msg);
            }


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
