package constants;

import gui.UserRecord;
import gui.UsersControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.17.
 */
public class XMLHelper {
    public static Logger logger =	LogManager.getLogger(UsersControl.class.getName());

    public static boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
        logger.debug("start method");
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (SAXException e) {
            logger.debug("Nevalidní xml", e);
            return false;

        } catch (IOException e) {
            logger.debug("IO chyba", e);
            return false;
        } finally {
            try {
                xml.close();
            } catch (IOException e) {
                logger.debug("IO chyba", e);
            }
            try {
                xsd.close();
            } catch (IOException e) {
                logger.debug("IO chyba", e);
            }
        }

    }
    public static void fillOldUsersXml(){
        logger.debug("start");
        try {
            File file =  new File( Constants.USERS_FILE);
            if(!file.exists()){
               boolean result = file.createNewFile();
                if(!result){
                    logger.error("Nemohu vytvořit nový soubour do kterého bych zapsal statistiky");
                    return;
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter( file));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.newLine();
            writer.write("<ROOT>\n" +
                    "\t<userRecords>");

            writer.newLine();
            for (UserRecord record: UserRecord.allRecords) {
                writer.write(record.toXml());
            }


            writer.write("\t</userRecords>\n" +
                    "</ROOT>");

            writer.flush();
            writer.close();
            logger.debug("end");
        } catch (Exception e) {
            logger.error("Chyba při ukládání user");
        }
    }
}
