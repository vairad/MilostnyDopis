package gui;


import constants.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

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
    public ProgressBar progressBar;

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
        logoutButton.setDisable(false);
        progressBar.setVisible(true);
        App.loginWorker = new Thread(App::connect);
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
        new Thread(App::logout).start();
    }

    @FXML
    public void onRefresh() {
        treeWiew.setDisable(true);
        Message msg = new Message(Event.ECH, MessageType.game, "");
        NetService.getInstance().sender.addItem(msg);
    }

    @FXML
    public void onNewGame() {
        int round_count = -1;
        while (round_count == -1){
            round_count = DialogFactory.gameCountDialog();
        }
        Message msg = new Message(Event.NEW, MessageType.game, ""+round_count);
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
        connectButton.setDisable(true);
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
     * @return repaired nickanme
     */
    String checkNickname(){
        logger.debug("Start method");
        String messageS;
        try {
             messageS = nickField.getText().trim();
        }catch (NullPointerException e){
            logger.error("null from text field");
            return null;
        }

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

    public void prepareLogin() {
        enableForm();
        disableGameMenu();
    }

    public void prepareGame() {
        disableForm();
        enableGameMenu();
    }

    public void setStatusText(String statusText) {
        this.statusText.setText(statusText);
    }

    public String getAddress() {
        return address.getText();
    }

    public void stopProgress() {
        progressBar.setVisible(false);
    }

    public void startProgress(){
        progressBar.setVisible(true);
    }
}
