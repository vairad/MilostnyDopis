package log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Třída spravující formát výpisů logu.
 * Jedináček
 *
 * @author Radek VAIS
 */
public class LogFormater extends Formatter {

    /**
     * Privátní konstruktor třídy formátovač
     */
    private LogFormater(){
        super();
    }

    /** instance třídy formátovač */
    private static LogFormater INSTANCE = new LogFormater();

    /** getter instance */
    public static LogFormater getInstance(){
        return INSTANCE;
    }

    /**
     * Vytvoří řetězec záznamu ve formátu:
     * pořadí. [LEVEL - Trida.metoda()] - Zpráva
     *
     * například:
     * <code>0. [INFO - Hlavni.main()] - Zacatek programu</code>
     *
     * @param record - záznam ke zpracování
     */
    @Override
    public String format(LogRecord record) {

        //poradove cislo zpravy
        long numRec = record.getSequenceNumber();

        //uroven zavaznosti zpravy
        String level = record.getLevel().getName();

        //nazev metody ve formatu Trida.metoda()
        String[] className = (record.getSourceClassName()).split("\\.");
        String methodName = className[1]+"."+record.getSourceMethodName()+"()";

        //zprava spojena se zaznamem
        String message = record.getMessage();

        //konstrukce zaznamu
        return numRec+". ["+level+" - "+methodName+"] - "+message+System.getProperty("line.separator");

    }


}