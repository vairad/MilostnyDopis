package message;

import game.Game;
import game.User;
import gui.App;
import javafx.application.Platform;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 14.11.16.
 */
public class MessageHandler extends Thread {

    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(MessageHandler.class.getName());

    private static boolean RunFlag = true;

    @Override
    public void run() {
        logger.debug("Start thread");
        while (MessageHandler.RunFlag){
            Message msg = NetService.getInstance().getMessageToServe();
            if(msg == null){
                logger.trace("null from queue");
                continue;
            }
            handleMessage(msg);
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.getType()){
            case servis:
                break;
            case game:
                handleTypeGame(msg);
                break;
            case login:
                handleTypeLogin(msg);
                break;
            case message:
                break;
            case unknown:
                break;
            default:
                logger.error("UNIMPLEMENTED MESSAGE TYPE");
                break;
        }
    }

    private void handleTypeGame(Message msg) {
        switch (msg.getEvent()){
            case ACK:
                handleGameACK(msg);
                break;
            case UNK:
                logger.error("UNKNOWN LOGIN EVENT TYPE");
                break;
            default:
                logger.error("UNIMPLEMENTED LOGIN EVENT TYPE");
                break;
        }
    }

    private void handleGameACK(Message msg) {
        String[] parts = msg.getMessage().split("=");

        if(parts.length != 2){
            logger.error("Wrong format of Game list response. (too many \"=\" )");
            return;
        }
        String[] gamesS = parts[1].split(";");
        List<Game> games  = new LinkedList<Game>();
        for (String gameS : gamesS ) {
            try {
                games.add(new Game(gameS));
            }catch (NumberFormatException e){
                logger.debug("Wrong format of players count in message");
            }
        }
        Platform.runLater(() -> App.fillTree(games));

    }

    private void handleTypeLogin(Message msg) {
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

    private void handleLoginECH(String message) {
        logger.debug("Start method - " + message);
        // probably no reaction
    }

    private void handleLoginNAK(String message) {
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


    private void handleLoginACK(String message) {
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
