package net;

/**
 * Created by XXXXXXXXXXXXXXXX on 13.1.16.
 */
public class MessageFactory {

    private static String message_pattern = "XXX#ddd#nnnnnnnnn#";


    public static String serverMessage(String data){
        String server_message = message_pattern;
        server_message = server_message.replace("XXX", "SRV"); // nastav opt code na SRV
        server_message += data;
        server_message += "##";

        int bytesCount = server_message.getBytes().length;
        String msg_bytes = String.format("%03d", bytesCount);
        server_message = server_message.replace("ddd", msg_bytes);

        return server_message;
    }

    public static String gameMessage(String data){
        String server_message = message_pattern;
        server_message = server_message.replace("XXX", "GAM"); // nastav opt code na SRV
        server_message += data;
        server_message += "##";

        int bytesCount = server_message.getBytes().length;
        String msg_bytes = String.format("%03d", bytesCount);
        server_message = server_message.replace("ddd", msg_bytes);

        return server_message;
    }

    public static String ackMessage(int msgNumber){
        String server_message = message_pattern;
        server_message = server_message.replace("XXX", "ACK"); // nastav opt code na SRV
        server_message += String.format("%09d", msgNumber);
        server_message += "##";

        int bytesCount = server_message.getBytes().length;
        String msg_bytes = String.format("%03d", bytesCount);
        server_message = server_message.replace("ddd", msg_bytes);

        return server_message;
    }
}
