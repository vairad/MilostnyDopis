package message;

import gui.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public class Message {
    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(Message.class.getName());

    int byteLen = 0;
    Event event = Event.UNK;
    MessageType type = MessageType.unknown;


    @Override
    public String toString(){
        return "###" + String.format("%04d", byteLen) +  MessageType +
    }
}
