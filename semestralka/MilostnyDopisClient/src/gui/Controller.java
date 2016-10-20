package gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import netservice.NetService;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    /** instance loggeru hlavni tridy */
    public static Logger Logger =	LogManager.getLogger(Controller.class.getName());

    public TextField port;
    public TextField ip4;
    public TextField ip3;
    public TextField ip2;
    public TextField ip1;
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
        boolean resultIP = true, resultPort = false;
        try {
            resultIP &= NetService.checkIpOctet(ip1.getText());
            resultIP &= NetService.checkIpOctet(ip2.getText());
            resultIP &= NetService.checkIpOctet(ip3.getText());
            resultIP &= NetService.checkIpOctet(ip4.getText());
            resultPort = NetService.checkPort(port.getText());
        }catch (NumberFormatException e){
            Controller.Logger.error("Ip or port parsing error", e);
            //todo err window that shows number format exception
        }

        if(!resultIP){
            Controller.Logger.error("Wrong range of ip");
            //todo err window that shows wrong IP was set
            return;
        }

        if(!resultPort){
            Controller.Logger.error("Wrong range of port");
            //todo err window that shows wrong Port
            return;
        }

        // todo run message list
        statusText.setText("Pokus o připojení k serveru");
    }

    @FXML
    public void onDefaultConnection(ActionEvent actionEvent){
        ip1.setText("127");
        ip2.setText("12");
        ip3.setText("34");
        ip4.setText("56");
        port.setText("2525");
        nickField.setText("Uni");
    }
}
