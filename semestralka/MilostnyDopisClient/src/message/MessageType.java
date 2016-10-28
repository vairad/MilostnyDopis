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
}
