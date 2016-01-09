package gui;

import net.NetService;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.16.
 */
public class Main {

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static {
        System.setProperty("java.util.logging.config.class","test.LogConfig");
    }

    /** instance loggeru hlavni tridy */
    public static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        NetService nt = null;
        int port = 1234;
        String address = "127.0.0.1";
        try {
            nt = new NetService(1234, address);
            logger.info("NetService je pripraveny na "+address+":"+port);
        } catch (SocketException e) {
            logger.severe("Nedostal jsem socket");
        } catch (UnknownHostException e){
            logger.severe("Neznámý host");
        }

        if(nt != null) {
            new GUIInter(nt);
        }else{
            logger.severe("NetSerice není inicializovaný");
        }

    }

}
