package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import netservice.NetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by XXXXXXXXXXXXXXXX on 1.11.16.
 */
public class GameWindow extends Window {

    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(GameWindow.class.getName());

    private Stage stage;
    ResourceBundle labels;


    public  void show(){
        logger.debug("start Window");
        stage = new Stage();

        loadView(new Locale("cs", "CZ"));

        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.setMaximized(true);
        stage.show();
    }

    private void loadView(Locale locale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            labels = ResourceBundle.getBundle("Texts", locale);
            fxmlLoader.setResources(labels);

            Parent root = fxmlLoader.load(App.class.getResource("gameScreen.fxml").openStream());

            Scene scene = new Scene(root, 300, 500);
            scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());

            stage.setTitle(labels.getString("TITLE")
                                    + " na serveru: "
                                    + NetService.getInstance().getServerName());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
