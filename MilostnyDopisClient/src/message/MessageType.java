package message;

/**
 * Created by Radek VAIS on 28.10.16.
 */
public enum MessageType {
    unknown ("UNK"),
    servis ("SER"),
    game ("GAM"),
    login ("LOG"),
    message ("MSG");

    private final String name;

    MessageType(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

    public static MessageType getTypeFromOpt(String substring) {
        for( MessageType t : MessageType.values()){
            if(t.toString().equals(substring)){
                return t;
            }
        }
        return unknown;
    }
}
