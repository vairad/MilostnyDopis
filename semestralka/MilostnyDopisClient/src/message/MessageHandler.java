package message;

import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by XXXXXXXXXXXXXXXX on 14.11.16.
 */
public class MessageHandler extends Thread {

    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(MessageHandler.class.getName());

    @Override
    public void run() {
        logger.debug("Start thread");
        while (NetService.isRunning()){
            Message msg;
            try {
                msg = NetService.getInstance().getMessageToServe();
            } catch (InterruptedException e) {
                logger.fatal("Message Handler is out -- caused interupted exception");
                return;
            }
            if(msg == null){
                logger.trace("null from queue");
                continue;
            }
            if(Thread.currentThread().isInterrupted()){
                return;
            }
            handleMessage(msg);
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.getType()){
            case servis:
                break;
            case game:
                GameHandler.handleTypeGame(msg);
                break;
            case login:
                LoginHandler.handleTypeLogin(msg);
                break;
            case message:
                break;
            case unknown:
                logger.debug("UNknown MESSAGE TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED MESSAGE TYPE");
                break;
        }
    }



}
