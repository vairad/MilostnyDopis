package gui;

import constants.Constants;
import constants.XMLHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 9.1.17.
 */
public class UsersControl extends HBox{

    private static final String  USER_RECORD = "userRecord" ;
    private static Logger logger =	LogManager.getLogger(UsersControl.class.getName());

    private static final int ID_ATR_PLAYER = 0;
    private static final int NICK_ATR_PLAYER = 1;
    private static final int SERVER_ATR_PLAYER = 2;
    private static final String USER_RECORDS = "userRecords";

    private InputStream xsd;

    @FXML public ComboBox<UserRecord> users;
    @FXML public Label label;

    private ResourceBundle bundle;

    public UsersControl(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "usersControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        bundle = App.bundle;
        fxmlLoader.setResources(App.bundle);

        getStylesheets().add(CardControl.class.getResource("usersControl.css").toExternalForm());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        try {
            xsd = UsersControl.class.getResource("userRecords.xsd").openStream();
        } catch (Exception e) {
            logger.error("No .xsd File");
            return;
        }

        if(!loadOldPlayers()){
            this.setDisable(true);
        }

    }

    private boolean loadOldPlayers() {
        List<UserRecord> usersList = loadXML();

        if(usersList.size() <= 0){
            return false;
        }

        UserRecord.allRecords = usersList;

        ObservableList<UserRecord> options = FXCollections.observableArrayList(usersList);
        users.setItems(options);
        return true;
    }

    private List<UserRecord> loadXML() {
        InputStream xsdIS = null;
        InputStream xmlIS = null;
        FileInputStream xmlSource = null;
        File file = null;
        try{
            file = new File(Constants.USERS_FILE);
            xmlSource = new FileInputStream(file);
        }catch (IOException e){
            logger.trace("file load problem");
            return new LinkedList<>();
        }

        try {
            xmlIS = xmlSource;
            xsdIS = xsd;

            if(validateAgainstXSD(xmlIS, xsdIS )){
                xmlIS = new FileInputStream(file);
                return parseXML(xmlIS);
            }else{
                return new LinkedList<>();
            }

        } catch (Exception e) {
            logger.debug("Chyba při načítání xml dokumentu", e);
        }finally {
            //clean mess
            if (xsdIS != null) try {
                xsdIS.close();
                xsd.close();
            } catch (IOException e) {
                logger.debug("IO chyba xsd", e);
            }
            if (xmlIS != null) try {
                xmlIS.close();
            } catch (IOException e) {
                logger.debug("IO chyba xml", e);
            }
        }
        return new LinkedList<>();
    }

    private List<UserRecord> parseXML(InputStream xmlIS) throws ParserConfigurationException, IOException, SAXException {
        logger.debug("start method");
        LinkedList<UserRecord> usersList  = new LinkedList<>();
        Document serverXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlIS);

     //   Node userCollection = serverXmlDocument.getElementsByTagName(USER_RECORDS).item(0);
        NodeList usersNodeList = serverXmlDocument.getElementsByTagName(USER_RECORD);
        for (int index = 0; index < usersNodeList.getLength(); index++) {
            Node userNode = usersNodeList.item(index);
            usersList.add(parseUserXML(userNode));
        }
        return  usersList;
    }

    private boolean validateAgainstXSD(InputStream xmlIS, InputStream xsdIS) {
        logger.debug("start method");
        return XMLHelper.validateAgainstXSD(xmlIS, xsdIS);
    }

    private UserRecord parseUserXML(Node playerNode) {
        logger.debug("start method");
        NodeList playerNodes = playerNode.getChildNodes();

        String idS = "";
        String serverS = "";
        String nickS = "";
        int idAttr = 0;

        while (idAttr < playerNodes.getLength()){
            Node playerAttribute = playerNodes.item(idAttr);
            if(playerAttribute.getNodeName().equals("id")){
                idS = playerAttribute.getFirstChild().getNodeValue();
                logger.trace("ID : " + idS);
            }
            if(playerAttribute.getNodeName().equals("server")){
                serverS = playerAttribute.getFirstChild().getNodeValue();
                logger.trace("SERVER : " + serverS);
            }
            if(playerAttribute.getNodeName().equals("nick")){
                nickS = playerAttribute.getFirstChild().getNodeValue();
                logger.trace("NICK : " + nickS);
            }
            idAttr++;
        }

        return new UserRecord(idS, nickS, serverS);
    }

    public void refresh(){
        ObservableList<UserRecord> options = FXCollections.observableArrayList(UserRecord.allRecords);
        users.setItems(options);
    }
}
