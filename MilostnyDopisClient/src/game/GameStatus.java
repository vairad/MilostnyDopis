package game;

import constants.PlayerPosition;
import constants.XMLHelper;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by XXXXXXXXXXXXXXXX on 17.12.16.
 */
public class GameStatus {

    /** pořadí atributů ve členu PLAYER */
    private static final int ORDER_ATR_PLAYER = 0;
    private static final int NICK_ATR_PLAYER = 1;
    private static final int ID_ATR_PLAYER = 2;
    private static final int ALIVE_ATR_PLAYER = 3;
    private static final int TOKEN_ATR_PLAYER = 4;
    private static final int GUARDED_ATR_PLAYER = 5;
    private static final int POINTS_ATR_PLAYER = 6;
    private static final int CARDS_ATR_PLAYER = 7;

    /** pořadí atributů ve členu CARD */
    private static final int TYPE_ATR_CARD = 0;
    private static final int NAME_ATR_CARD = 1;


    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(GameStatus.class.getName());

    Document serverXmlDocument;

    InputStream xsd;

    LinkedList<Player> players;
    private String uid;
    private Long seqNumber;
    private int round;
    private int roundCount;

    public GameStatus(String serverMessage){
        logger.debug("start method");
        createGameFields();

        logger.info(serverMessage);

        try {
            xsd = Game.class.getResource("gameStatus.xsd").openStream();
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
        logger.debug("start method");
        players = new LinkedList<>();
    }

    private void parseXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("start method");
        serverXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlIS);

        //load players from xml
        Node playersCollection = serverXmlDocument.getElementsByTagName("playersCollection").item(0);
        NodeList playersNodeList = playersCollection.getChildNodes();
        for (int index = 0; index < playersNodeList.getLength(); index++) {
            Node playerNode = playersNodeList.item(index);
            parsePlayerXML(playerNode);
        }

        // sort players depends on order
        Collections.sort(players);

        // compute display order of players for local gui
        setPlayersDisplayOrder();

        //load game uid from xml
        Node gameUid = serverXmlDocument.getElementsByTagName("gameStatus").item(0).getFirstChild();
        this.uid = gameUid.getFirstChild().getNodeValue();
        logger.trace("Game uid: "+ this.uid);

        // load sequence number from xml
        Node seq = serverXmlDocument.getElementsByTagName("seq").item(0).getFirstChild();
        this.seqNumber = Long.parseUnsignedLong(seq.getNodeValue());
        logger.trace("Sequence number of game status: " + this.seqNumber);

        // load sequence number from xml
        Node round = serverXmlDocument.getElementsByTagName("round").item(0).getFirstChild();
        this.round = Integer.parseInt(round.getNodeValue());
        logger.trace("Sequence number of game status: " + this.round);

        // load sequence number from xml
        Node roundCount = serverXmlDocument.getElementsByTagName("roundCount").item(0).getFirstChild();
        this.roundCount = Integer.parseInt(roundCount.getNodeValue());
        logger.trace("Sequence number of game status: " + this.roundCount);
    }

    private void setPlayersDisplayOrder() {
        logger.debug("start method");
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

        Node playerAttribute = playerNodes.item(ORDER_ATR_PLAYER);
        String orderS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Poradi : " + orderS);

        playerAttribute = playerNodes.item(NICK_ATR_PLAYER);
        String nickS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Prezdivka : " + nickS);

        playerAttribute = playerNodes.item(ID_ATR_PLAYER);
        String uidS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("ID : " + uidS);


        playerAttribute = playerNodes.item(ALIVE_ATR_PLAYER);
        String aliveS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Alive : " + aliveS);

        playerAttribute = playerNodes.item(TOKEN_ATR_PLAYER);
        String tokenS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Token : " + tokenS);

        playerAttribute = playerNodes.item(GUARDED_ATR_PLAYER);
        String guardedS = playerAttribute.getFirstChild().getNodeValue();
        logger.trace("Guarded : " + guardedS);

        playerAttribute = playerNodes.item(POINTS_ATR_PLAYER);
        int points = Integer.parseInt(playerAttribute.getFirstChild().getNodeValue());
        logger.trace("Body : " + points);

        playerAttribute = playerNodes.item(CARDS_ATR_PLAYER);
        List<Card> cardList = parseCardListXML(playerAttribute);

        Player p = new Player(nickS
                        , uidS
                        , Integer.parseInt(orderS)
                        , Boolean.parseBoolean(aliveS)
                        , Boolean.parseBoolean(tokenS)
                        , Boolean.parseBoolean(guardedS)
                        , points);
        p.resetCards(cardList);

        players.add(p);
    }

    private List<Card> parseCardListXML(Node cardsCollection) {
        logger.debug("start method");
        LinkedList<Card> cardList = new LinkedList<>();

        NodeList cardsNodeList = cardsCollection.getChildNodes();
        for (int index = 0; index < cardsNodeList.getLength(); index++) {
            Node cardNode = cardsNodeList.item(index);
            cardList.add(parseCardXML(cardNode));
        }

        return cardList;
    }

    private Card parseCardXML(Node cardNode) {
        logger.debug("start method");
        NodeList cardNodes = cardNode.getChildNodes();

        Node cardAttribute = cardNodes.item(TYPE_ATR_CARD);
        String typeS = cardAttribute.getFirstChild().getNodeValue();
        logger.trace("Typ karty : " + typeS);

        cardAttribute = cardNodes.item(NAME_ATR_CARD);
        String nameS = cardAttribute.getFirstChild().getNodeValue();
        logger.trace("Nazev : " + nameS);

        return Card.getCardFromInt(Integer.parseInt(typeS));
    }


    private static boolean validateAgainstXSD(InputStream xml, InputStream xsd) {
        logger.debug("start method");
        return XMLHelper.validateAgainstXSD(xml,xsd);
    }

    public String getUid() {
        return uid;
    }

    public Long getSeqNumber() {
        return seqNumber;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getRound() {
        return round;
    }
}
