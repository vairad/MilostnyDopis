package message;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public enum Event {
    UNK ("UNK"),
    ACK ("ACK"),
    NAK ("NAK"),
    ECH ("ECH"),
    NEW ("NEW"),
    OUT ("OUT"),
    COD ("COD"),
    NEP ("NEP"),
    CAR ("CAR"),
    TOK ("TOK"),
    STA ("STA"),
    PLA("PLA");

    private final String name;

    Event(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

    public static Event getEventFromOpt(String substring) {
        for( Event e : Event.values()){
            if(e.toString().equals(substring)){
                return e;
            }
        }
        return UNK;
    }
}
