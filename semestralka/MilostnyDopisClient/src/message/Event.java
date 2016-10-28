package message;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public enum Event {
    UNK ("UNK"),
    ACK ("ACK"),
    NAK ("NAK"),
    ECH ("ECH");

    private final String name;

    Event(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}