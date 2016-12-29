package game;

import constants.PlayerPosition;
import gui.App;
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
import java.util.Collections;
import java.util.LinkedList;

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

    InputStream xsd;

    LinkedList<Player> players;
    private String uid;

    public GameStatus(String serverMessage){

        createGameFields();

        try {
            xsd = Game.class.getResource("test.xsd").openStream();
        } catch (IOException e) {
            logger.error("No .xsd File");
            return;
        }

        InputStream xsdIS = null;
        InputStream xmlIS = null;
        try {
            xmlIS = new ByteArrayInputStream(serverMessage.getBytes(StandardCharsets.UTF_8));
            xsdIS = xsd;

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
                xsd.close();
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
        players = new LinkedList<>();

    }

    private void parseXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("start method");
        serverXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlIS);

        Node playersCollection = serverXmlDocument.getElementsByTagName("playersCollection").item(0);
        NodeList playersNodeList = playersCollection.getChildNodes();
        for (int index = 0; index < playersNodeList.getLength(); index++) {
            Node playerNode = playersNodeList.item(index);
            parsePlayerXML(playerNode);
        }

        Collections.sort(players);

        setPlayersDisplayOrder();
        
        Node gameUid = serverXmlDocument.getElementsByTagName("gameStatus").item(0).getFirstChild();
        this.uid = gameUid.getFirstChild().getNodeValue();

    }

    private void setPlayersDisplayOrder() {
        while (!players.peekFirst().isLocal()){
            players.addFirst(players.pollLast());
        }
        for (int index = 0; index < players.size(); index++){
            switch (index) {
                case 0:
                    players.get(index).setDisplay_order(PlayerPosition.LOCAL);
                    break;
                case 1:
                    players.get(index).setDisplay_order(PlayerPosition.LEFT);
                    break;
                case 2:
                    players.get(index).setDisplay_order(PlayerPosition.CENTER);
                    break;
                case 3:
                    players.get(index).setDisplay_order(PlayerPosition.RIGHT);
                    break;
            }
        }

    }

    private void parsePlayerXML(Node playerNode) {
        logger.debug("start method");
        NodeList playerNodes = playerNode.getChildNodes();

        Node playerAttribute = playerNodes.item(ORDER_ATR);
        String orderS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Poradi : " + orderS);

        playerAttribute = playerNodes.item(NICK_ATR);
        String nickS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Prezdivka : " + nickS);

        playerAttribute = playerNodes.item(ID_ATR);
        String uidS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("ID : " + uidS);

        players.add(new Player(nickS, uidS, Integer.parseInt(orderS)));
    }


    private static boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
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

    public String getUid() {
        return uid;
    }
}
