package message;

/**
 * Created by XXXXXXXXXXXXXXXX on 4.1.17.
 */
public class MessageFactory {
    public static Message getCards(){
        return new Message(Event.CAR, MessageType.game, "CARD");
    }
    public static Message getStatusMessage(String gameUid) {
        return new Message(Event.STA, MessageType.game, gameUid);
    }
    public static Message getGameList() {
        return new Message(Event.ECH, MessageType.game, "");
    }
}
