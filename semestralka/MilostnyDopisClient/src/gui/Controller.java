package gui;

import javafx.scene.control.Alert;
import message.Event;
import message.Message;
import message.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import netservice.NetService;
import sun.nio.ch.Net;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    /** instance loggeru hlavni tridy */
    public static Logger Logger =	LogManager.getLogger(Controller.class.getName());

    public TextField port;
    public TextField address;

    public Text labelTitle;
    public TextField nickField;
    @FXML
    private Text statusText;
    @FXML
    private Label labelServer;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
//        labelServer.setText(bundle.getString("serverAdressTitle"));
    }

    @FXML
    public void onConnect(ActionEvent actionEvent) {
        boolean resultIP = true;
        int resultPort;
        try {
            resultPort = NetService.checkPort(port.getText());
        }catch (NumberFormatException e){
            Controller.Logger.error("Error port number", e);
            resultPort = -1;
        }

        if(resultPort == -1){
            Controller.Logger.error("Wrong range of port");

            Alert alert  = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("portErrorTitle"));
            alert.setHeaderText(bundle.getString("portErrorHeadline"));
            alert.setContentText(bundle.getString("portErrorText"));
            alert.showAndWait();
            return;
        }

        try{
            statusText.setText("Pokus o připojení k serveru");
            NetService.getInstance().setup(address.getText(), resultPort);
            NetService.getInstance().initialize();
        }catch (IOException e){
            statusText.setText("Nelze se připojit k serveru: " + e.getLocalizedMessage());
            Controller.Logger.error("IO Error", e);
            return;
        }
        statusText.setText("Probíhá přihlášení");

      //  NetService.nickNameHandShake(nickField.getText());
    }

    @FXML
    public void onDefaultConnection(ActionEvent actionEvent){
        address.setText("localhost");
        port.setText("2525");
        nickField.setText("Uni");
    }


    public void onEcho(ActionEvent actionEvent) {
        Message msg = new Message(Event.ECH, MessageType.message, "Ahoj");
        NetService.getInstance().sender.addItem(msg);
    }
}
