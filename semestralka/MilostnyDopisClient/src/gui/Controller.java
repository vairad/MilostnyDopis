package gui;

import constants.Constants;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import message.Event;
import message.Message;
import message.MessageType;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Třída která zprostředkovává zpracvoání událostí v přihlašovacím okně aplikace.
 * Všechny atributy jsou privátní. Přistupuje se k nim pouze pomocí metod.
 *
 * @author Radek VAIS
 */
public class Controller implements Initializable {

    /** instance loggeru hlavni tridy */
    private static Logger logger =	LogManager.getLogger(Controller.class.getName());

    @FXML private Label allGamesCheckLabel;
    @FXML private TextField portTextField;
    @FXML private TextField addressTextField;
    @FXML private TextField nickTextField;
    @FXML private Button refreshButton;

    @FXML private Button newGameButton;
    @FXML private Button connectButton;
    @FXML private Button defaultConnectionButton;
    @FXML private Button logoutButton;
    @FXML private ProgressBar progressBar;
    @FXML private UsersControl userControl;
    @FXML private CheckBox allGamesCheck;
    @FXML private Text statusText;
    @FXML private TreeView<GameRecord> treeView;

    private ResourceBundle bundle;

    /**
     * Nastaví parametry a lokalizační soubor
     * @param location
     * @param resources lokalizační soubor s texty
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("start method");
        bundle = resources;

        allGamesCheck.setOnAction(event -> {
            logger.trace("Change property");
            App.smartFillTree();
        });

        noLoggedForm();
    }

    /**
     * Spustí připojování serveru v samostatném vlákně
     * @see gui.App
     */
    @FXML
    public void onConnect(){
        logger.debug("start method");
        disableForm();
        logoutButton.setDisable(false);
        progressBar.setVisible(true);
        App.loginWorker = new Thread(App::connect);
        App.loginWorker.start();
    }

    /**
     * Vyplní pole přihlašovací pole standardními údaji
     */
    @FXML
    public void onDefaultConnection(){
        logger.trace("Start method");
        addressTextField.setText("localhost");
        portTextField.setText("2525");
        nickTextField.setText("Řeřicha");
    }

    /**
     * Spustí odhlašovací vlákno
     */
    @FXML
    public void onLogout() {
        logger.trace("Start method");
        setStatusText(bundle.getString("loggingOut"));
        NetService.getInstance().stop();
        Thread thread = new Thread(App::logout);
        App.addWorker(thread);
    }

    /**
     * Zadá správu pro získání nového seznamu her
     */
    @FXML
    public void onRefresh() {
        logger.trace("start method");
        treeView.setDisable(true);
        Message msg = new Message(Event.ECH, MessageType.game, "");
        NetService.getInstance().sender.addItem(msg);
    }

    /**
     * Spustí dialogy pro vytvoření nové hry
     */
    @FXML
    public void onNewGame() {
        logger.trace("start method");
        int round_count, player_count;

        round_count = DialogFactory.gameCountDialog();
        if(round_count < Constants.MIN_POSSIBLE_ROUNDS
                && round_count > Constants.MAX_POSSIBLE_ROUNDS){
            return;
        }

        player_count = DialogFactory.playerCountDialog();
        if(player_count < Constants.MIN_POSSIBLE_PLAYERS
                && player_count > Constants.MAX_POSSIBLE_PLAYERS){
            return;
        }

        Message msg = new Message(Event.NEW, MessageType.game, ""+round_count+"&&"+player_count);
        NetService.getInstance().sender.addItem(msg);
        onRefresh();
    }

    /**
     * Vyvolá okno se statistikami sítě
     */
    @FXML
    public void onStats(){
        logger.trace("start method");
        DialogFactory.messagesResult(NetService.sendBytes
                                     ,NetService.recvBytes
                                     , App.reconnections);
    }
//=============================================================================================================
//=============================================================================================================
//=============================================================================================================

    /**
     * Nastaví formulář pro nepříhlášeného uživatele
     */
    void noLoggedForm() {
        logger.debug("start method");
        disableGameMenu();
        stopProgress();
        enableForm();
        logoutButton.setDisable(true);
        connectButton.setDisable(false);
    }

    /**
     * Nastaví formulář pro přihlášeného uživatele
     * */
    void loggedForm() {
        logger.debug("start method");
        enableGameMenu();
        logoutButton.setDisable(false);
        connectButton.setDisable(true);
        disableForm();
        onRefresh();
    }

    /**
     * Nastaví všechny prvky Formuláře pro získání dat na neaktivní
     */
    private void disableForm() {
        logger.debug("start method");
        portTextField.setDisable(true);
        addressTextField.setDisable(true);
        nickTextField.setDisable(true);
        defaultConnectionButton.setDisable(true);
        userControl.setDisable(true);
    }

    /**
     * Nastaví všechny formulářové prvky na aktivní
     */
    private void enableForm() {
        logger.debug("start method");
        portTextField.setDisable(false);
        addressTextField.setDisable(false);
        nickTextField.setDisable(false);
        defaultConnectionButton.setDisable(false);
        userControl.setDisable(false);
    }

    /**
     * Nastaví všechny prvky volby her na aktivní
     */
    void enableGameMenu() {
        logger.debug("start method");
        treeView.setDisable(false);
        refreshButton.setDisable(false);
        newGameButton.setDisable(false);
        allGamesCheck.setDisable(false);
        allGamesCheckLabel.setDisable(false);
    }

    /**
     * Nastaví všechny prvky volby he na NEaktivní
     */
    private void disableGameMenu() {
        logger.debug("start method");
        treeView.setDisable(true);
        refreshButton.setDisable(true);
        newGameButton.setDisable(true);
        allGamesCheck.setDisable(true);
        allGamesCheckLabel.setDisable(true);
    }

    /**
     * Vrací zadanou přezdívku uživatele v mezích
     * Constants.NICKNAME_MIN_LENGTH a
     * Constants.NICKNAME_MAX_LENGTH
     * Může být null
     * @return repaired nickanme or null
     */
    String checkNickname(){
        logger.debug("Start method");
        String messageS;
        try {
             messageS = nickTextField.getText().trim();
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

    /**
     * Vrací odkaz na strom her
     * @return treeView of game records
     */
    TreeView<GameRecord> getTreeWiew() {
        return treeView;
    }

    /**
     * Nastaví zprávu status textu
     * @param statusText zpráva k zobrazení
     */
    void setStatusText(String statusText) {
        this.statusText.setText(statusText);
    }

    /**
     * Vrací obsah pole adresy serveru
     * @return obsah pole adresy serveru
     */
    String getAddressTextField() {
        return addressTextField.getText();
    }

    /**
     * Zastaví (skryje) progress bar.
     */
    void stopProgress() {
        progressBar.setVisible(false);
    }

    /**
     * Rozběhne (zobrazí) progress bar.
     */
    void startProgress(){
        progressBar.setVisible(true);
    }

    /**
     * Vyplní údaje o přihlášení pomocí user Record
     * Slouží k vyplnění automatického záznamu.
     * @param user záznam o uživateli k přihlášení
     */
    void setUpUser(UserRecord user) {
        nickTextField.setText(user.getNick());
        portTextField.setText(user.getPort());
        addressTextField.setText(user.getServerName());
    }

    /**
     * Nastaví status message na hlášku nejsi přihlášen
     */
    void setNoLoggedStatus() {
        if(statusText.getText().equals(App.bundle.getString("loginInProgress"))){
            statusText.setText(App.bundle.getString("noLogged"));
        }
    }

    /**
     * Vrací hodnotu checkbocu všechny hry
     * @return allGamesCheck.isSelected()
     */
    public boolean isAllGames() {
        return allGamesCheck.isSelected();
    }

    /**
     * Vrací hodnotu pole portu
     * @return text value of portTextField
     */
    public String getPort() {
        return portTextField.getText();
    }

    /**
     * Vyvolá aktualizavi prvku záznamů předchozích přihlášení
     */
    public void refreshUserRecords() {
        userControl.refresh();
    }
}
