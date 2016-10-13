package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {
    public TextField port;
    public TextField ip4;
    public TextField ip3;
    public TextField ip2;
    public TextField ip1;
    @FXML
    private Text actiontarget;
    @FXML
    private Label labelName;

    @FXML
    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        System.out.println("Handle button");
        actiontarget.setText("Sign in button pressed");
        labelName.setText("To je easy");
    }
}
