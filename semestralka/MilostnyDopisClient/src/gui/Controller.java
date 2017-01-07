package gui;


import constants.Constants;
import game.Player;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import message.Event;
import message.Message;
import message.MessageHandler;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class Controller implements Initializable {
    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Controller.class.getName());

    @FXML
    public TextField port;
    @FXML
    public TextField address;
    @FXML
    public Text labelTitle;
    @FXML
    public TextField nickField;
    @FXML
    public Button refreshButton;
    @FXML
    public Button newGameButton;
    @FXML
    public Button connectButton;
    @FXML
    public Button defaultConnectionButton;
    @FXML
    public Button logoutButton;
    @FXML
    private Text statusText;
    @FXML
    private TreeView<GameRecord> treeWiew;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        noLoggedForm();
    }

    @FXML
    public void onConnect(){
        disableForm();
            //todo start loading gui
        App.loginWorker = new Thread(() -> {
            if(!NetService.isRunning()){
                connect();
            }

            if (!Thread.currentThread().isInterrupted()) {//todo handle interuption

            }

            sendLogin();
        });
        App.loginWorker.start();
    }

    @FXML
    public void onDefaultConnection(){
        logger.debug("Start method");
        address.setText("localhost");
        port.setText("2525");
        nickField.setText("Uni");
    }

    @FXML
    public void onLogout() {
        logger.debug("Start method");
        if(!Player.getLocalPlayer().isLogged()){
            return;
        }
        Message msg = new Message(Event.OUT, MessageType.login, Player.getLocalPlayer().getServerUid() );
        for(int i = 0; i < 10; i++) {
            NetService.getInstance().sender.addItem(msg);
        }
        noLoggedForm();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            logger.trace("Interupted wait for threads");
        }
        NetService.getInstance().destroy();
    }

    @FXML
    public void onRefresh() {
        treeWiew.setDisable(true);
        Message msg = new Message(Event.ECH, MessageType.game, "");
        NetService.getInstance().sender.addItem(msg);
    }

    @FXML
    public void onNewGame() {
        Message msg = new Message(Event.NEW, MessageType.game, "");
        NetService.getInstance().sender.addItem(msg);
        onRefresh();
    }

//=============================================================================================================

    void noLoggedForm() {
        disableGameMenu();
        logoutButton.setDisable(true);
        enableForm();
    }



    void loggedForm() {
        enableGameMenu();
        logoutButton.setDisable(false);
        connectButton.setDisable(true);
        disableForm();
        onRefresh();
    }

    /**
     *
     */
    void disableForm() {
        port.setDisable(true);
        address.setDisable(true);
        nickField.setDisable(true);
        defaultConnectionButton.setDisable(true);
        statusText.setText(bundle.getString("loggedIn"));
    }

    /**
     *
     */
    void enableForm() {
        port.setDisable(false);
        address.setDisable(false);
        nickField.setDisable(false);
        connectButton.setDisable(false);
        defaultConnectionButton.setDisable(false);
        //statusText.setText(bundle.getString("headline"));
    }


    /**
     *
     */
    private void sendLogin(){
        logger.debug("Start method");
        String messageS = checkNickname();
        if(messageS == null){
            logger.trace("no nickname");
            return;
        }
        Message msg = new Message(Event.ECH, MessageType.login, messageS);
        NetService.getInstance().sender.addItem(msg);
    }

    /**
     *
     * @return repaired nickanme
     */
    private String checkNickname(){
        logger.debug("Start method");
        String messageS = nickField.getText().trim();

        if(messageS.length() < Constants.NICKNAME_MIN_LENGTH){
            DialogFactory.alertError( bundle.getString("nickErrTitle")
                    , bundle.getString("nickErrHeadline")
                    , bundle.getString("nickErrTextShort"));
            return null;
        }

        if(messageS.length() > Constants.NICKNAME_MAX_LENGTH){
            DialogFactory.alertError( bundle.getString("nickErrTitle")
                    , bundle.getString("nickErrHeadline")
                    , bundle.getString("nickErrTextLong"));
            return null;
        }

        return messageS;
    }

    /**
     *
     */
    private void connect() {
        logger.debug("Start method");

        int resultPort;
        try {
            resultPort = NetService.checkPort(port.getText());
        }catch (NumberFormatException e){
            logger.error("Error port number", e);
            resultPort = -1;
        }

        if(resultPort == -1){
            logger.error("Wrong range of port");

            DialogFactory.alertError( bundle.getString("portErrorTitle")
                    , bundle.getString("portErrorHeadline")
                    , bundle.getString("portErrorText"));
            return;
        }

        try{
            statusText.setText(bundle.getString("connectionTry"));
            NetService.getInstance().setup(address.getText(), resultPort);
            NetService.getInstance().initialize();

            logger.trace("Iinit MessageHandler");
            NetService.messageHandler = new MessageHandler();
            logger.trace("start messageHandler");
            NetService.messageHandler.start();
            logger.trace("MessageHandler started");

        }catch (IOException e){
            statusText.setText(bundle.getString("notConnected") +" " + e.getLocalizedMessage());
            logger.error("IO Error", e);
            noLoggedForm();
            return;
        }
        statusText.setText(bundle.getString("loginInProgress"));
    }

    TreeView<GameRecord> getTreeWiew() {
        return treeWiew;
    }

    void enableGameMenu() {
        treeWiew.setDisable(false);
        refreshButton.setDisable(false);
        newGameButton.setDisable(false);
    }

    private void disableGameMenu() {
        treeWiew.setDisable(true);
        refreshButton.setDisable(true);
        newGameButton.setDisable(true);
    }
}
