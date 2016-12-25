package gui;


import game.Card;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ResourceBundle;


/**
 * Created by XXXXXXXXXXXXXXXX on 19.12.16.
 */
public class CardControl extends HBox {
    @FXML private Label labelValue;
    @FXML private Label labelName;
    private Card card;
    private ResourceBundle bundle;


    public CardControl() {
        bundle = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "cardControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        getStylesheets().add(CardControl.class.getResource("cardControl.css").toExternalForm());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
        labelName.setText(getCardText(card));
        labelValue.setText(Integer.toString(card.getValue()));
    }


    public StringProperty textProperty() {
        return labelName.textProperty();
    }

    @FXML
    protected void onClick() {
        System.out.println("The button was clicked!");
    }

    private String getCardText(Card card){
        try{
            return bundle.getString("CARD" + card.getValue());
        }catch (NullPointerException e){
            bundle = App.bundle;
            return bundle.getString("CARD" + card.getValue());
        }
    }
}