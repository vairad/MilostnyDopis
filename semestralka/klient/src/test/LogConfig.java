package test;

import gui.Main;
import net.NetService;
import net.Receiver;
import net.Sender;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interní kofigurace nastavení logování projektu
 *
 * @author Radek VAIS
 */
public class LogConfig {

    private static final String LOGOVACI_SOUBOR = "Milostny-Dopis.log";
    public static final String KODOVANI = "UTF-8";

    /**
     * Třída obsahující konfiguraci loggeru pro tento projekt
     * @throws IOException
     * @throws SecurityException
     */
    public LogConfig() throws SecurityException, IOException {

        //Vytvoření conzolového handleru
        ConsoleHandler consoleHandler = new ConsoleHandler();
        //Nastavení úrovně výpisů na tento handler
        consoleHandler.setLevel(Level.ALL);
        //Nastavení formátu, respektive formatteru na můj formátovač
        consoleHandler.setFormatter(LogFormater.getInstance());

        //vytvoreni soubor handler
        FileHandler fileHandler = new FileHandler(LOGOVACI_SOUBOR, true);
        //nastaveni formatu zprav
        fileHandler.setFormatter(LogFormater.getInstance());
        //nastaveni levelu
        fileHandler.setLevel(Level.ALL);
        //nastaveni kodovani souboru
        fileHandler.setEncoding(KODOVANI);

        //inicializace vsech handleru
        Main.logger = Logger.getLogger(Main.class.getName());
        NetService.logger = Logger.getLogger(NetService.class.getName());
        Receiver.logger = Logger.getLogger(Receiver.class.getName());
        Sender.logger = Logger.getLogger(Sender.class.getName());

        //nastavení úrovně výpisů loggeru
        Level level = Level.ALL;

        Main.logger.setLevel(level);
        NetService.logger.setLevel(level);
        Receiver.logger.setLevel(level);
        Sender.logger.setLevel(level);

        //připojení handlerů

        Main.logger.addHandler(consoleHandler);
        NetService.logger.addHandler(consoleHandler);
        Receiver.logger.addHandler(consoleHandler);
        Sender.logger.addHandler(consoleHandler);

      /*  Hlavni.hlavniLogger.addHandler(fileHandler);
        Generator.generatorLogger.addHandler(fileHandler);
        OsobniCislo.osCisloLogger.addHandler(fileHandler);*/
    }



}