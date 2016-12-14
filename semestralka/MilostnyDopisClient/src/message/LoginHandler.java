package message;

import game.User;
import gui.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by XXXXXXXXXXXXXXXX on 14.12.16.
 */
public class LoginHandler {

    /** instance loggeru tridy */
    public static Logger logger =	LogManager.getLogger(LoginHandler.class.getName());


    public static void handleTypeLogin(Message msg) {
        switch (msg.getEvent()){
            case ACK:
                handleLoginACK(msg.getMessage());
                break;
            case NAK:
                handleLoginNAK(msg.getMessage());
                break;
            case ECH:
                handleLoginECH(msg.getMessage());
                break;
            case UNK:
                logger.error("UNKNOWN LOGIN EVENT TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED LOGIN EVENT TYPE");
                break;
        }
    }

    private static void handleLoginECH(String message) {
        logger.debug("Start method - " + message);
        // probably no reaction
    }

    private static void handleLoginNAK(String message) {
        logger.debug("Start method - " + message);
        String[] messageParts = message.split("&&");
        if(messageParts.length < 2){
            logger.error("Špatný formát obsahu zprávy LOG ACK :" + message);
            return;
        }

        if(User.getInstance().getServerUid().equals(messageParts[0])){
            logger.trace("stejný uživatel :"+message);
            User.getInstance().setLogged(true);
            App.userLogged();
            return;
        }

        User.getInstance().setServerUid(messageParts[0]);
        String nick = "";
        if(messageParts.length > 2){
            //todo slepit string
        }else{
            nick = messageParts[1];
        }

        User.getInstance().setNick(nick);

        User.getInstance().setLogged(true);
        App.userLogged();
    }


    private static void handleLoginACK(String message) {
        logger.debug("Start method - " + message);
        String[] messageParts = message.split("&&");
        if(messageParts.length < 2){
            logger.error("Špatný formát obsahu zprávy LOG ACK :" + message);
            return;
        }

        User.getInstance().setServerUid(messageParts[0]);
        String nick = "";
        if(messageParts.length > 2){
            //todo slepit string
            logger.error("Alternativní tok... Nutné implementovat přezdívku s && oddělovačem");
        }else{
            nick = messageParts[1];
        }

        User.getInstance().setNick(nick);

        User.getInstance().setLogged(true);
        App.userLogged();
    }
}
