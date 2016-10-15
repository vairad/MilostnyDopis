package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
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
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        System.out.println("Handle button");
        statusText.setText("Pokus o připojení k serveru");
    }

    @FXML
    public void  handleLocallhost(ActionEvent actionEvent){
        ip1.setText("127");
        ip2.setText("12");
        ip3.setText("34");
        ip4.setText("56");
        port.setText("2525");
        nickField.setText("Uni");
    }
}
