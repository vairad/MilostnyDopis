package message;

/**
 * Created by XXXXXXXXXXXXXXXX on 4.1.17.
 */
public class MessageFactory {
    public static Message getCards(){
        return new Message(Event.CAR, MessageType.game, "CARD");
    }
    public static Message getStatusMessage(String uid) {
        return new Message(Event.STA, MessageType.game, uid);
    }
}
