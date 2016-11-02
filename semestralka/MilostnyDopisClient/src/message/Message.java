package message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public class Message {
    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(Message.class.getName());

    private static final int HEADER_LEN = 14;

    private int byteLen = 0;
    private Event event = Event.UNK;
    private MessageType type = MessageType.unknown;
    private String message = "";

    public int getByteLen() {
        return byteLen;
    }

    public Event getEvent() {
        return event;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Message(Event event, MessageType type, String message) {
        this.event = event;
        this.type = type;
        this.message = message.trim();
        this.byteLen = HEADER_LEN + this.message.length();
    }

    public Message(String message){
        this.type = MessageType.getTypeFromOpt(message.substring(7, 10));
        this.event = Event.getEventFromOpt(message.substring(10, 13));
        this.message = message.substring(14).trim();
        this.byteLen = HEADER_LEN + this.message.length();
    }

    @Override
    public String toString(){
        return "###" + String.format("%04d", byteLen) +  type + event + "#" + message;
    }
}
