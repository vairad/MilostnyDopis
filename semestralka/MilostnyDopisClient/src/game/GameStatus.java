package game;

import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by XXXXXXXXXXXXXXXX on 17.12.16.
 */
public class GameStatus {

    private static final int ORDER_ATR = 0;
    private static final int NICK_ATR = 1;
    private static final int ID_ATR = 2;


    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(GameStatus.class.getName());

    Document serverXmlDocument;

    File xsd;

    Player[] players;

    public GameStatus(String serverMessage){

        createGameFields();

        xsd = new File("test.xsd");

        InputStream xsdIS = null;
        InputStream xmlIS = null;
        try {
            xmlIS = new ByteArrayInputStream(serverMessage.getBytes(StandardCharsets.UTF_8));
            xsdIS = new FileInputStream(xsd);

            if(validateAgainstXSD(xmlIS, xsdIS )){
                xmlIS = new ByteArrayInputStream(serverMessage.getBytes(StandardCharsets.UTF_8));
                parseXML(xmlIS);
            }else{
                Message msg = new Message(Event.STA, MessageType.game, Game.getUid());
                NetService.getInstance().sender.addItem(msg);
            }

        } catch (Exception e) {
            logger.debug("Chyba při načítání xml dokumentu", e);
        }finally {
            //clean mess

            if(xsdIS != null) try {
                xsdIS.close();
            } catch (IOException e) {
                logger.debug("IO chyba xsd",e);
            }

            if(xmlIS != null) try {
                xmlIS.close();
            } catch (IOException e) {
                logger.debug("IO chyba xml",e);
            }
        }
    }

    private void createGameFields() {
        players = new Player[4];

    }

    private void parseXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("start method");
        serverXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlIS);
        Node playersCollection = serverXmlDocument.getElementsByTagName("playersCollection").item(0);
        NodeList playersNodeList = playersCollection.getChildNodes();
        for (int index = 0; index < playersNodeList.getLength(); index++) {
            Node playerNode = playersNodeList.item(index);
            parsePlayerXML(playerNode);

            logger.debug(playerNode.getFirstChild().getNodeValue());
        }
    }

    private void parsePlayerXML(Node playerNode) {
        logger.debug("start method");
        NodeList playerNodes = playerNode.getChildNodes();

        Node playerAttribute = playerNodes.item(ORDER_ATR);
        String tmp = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Poradi : " + tmp);

        playerAttribute = playerNodes.item(NICK_ATR);
        tmp = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Prezdivka : " + tmp);

        playerAttribute = playerNodes.item(ID_ATR);
        tmp = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("ID : " + tmp);

    }


    private static boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
        logger.debug("start method");
        try
        {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (SAXException e) {
            logger.debug("Nevalidní xml",e);
            return false;

        } catch (IOException e) {
            logger.debug("IO chyba", e);
            return false;
        }finally {
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

}
