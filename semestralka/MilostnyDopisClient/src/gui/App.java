package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    /*Statický inicializační blok nastavující odkaz (proměnnou) na konfiguraci loggeru*/
    static {
        System.setProperty("log4j.configurationFile","log-conf.xml");
    }

    /** instance loggeru hlavni tridy */
    public static Logger logger =	LogManager.getLogger(App.class.getName());

    private Stage stage;
    ResourceBundle labels;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        loadView(new Locale("cs", "CZ"));

        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.show();

    }

    private void loadView(Locale locale) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            labels = ResourceBundle.getBundle("Texts", locale);
            fxmlLoader.setResources(labels);

            Parent root = fxmlLoader.load(App.class.getResource("startScreen.fxml").openStream());

            Scene scene = new Scene(root, 300, 500);
            scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());

            stage.setTitle(labels.getString("TITLE"));
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        logger.info("Zacatek programu");
        launch(args);
        logger.info("Kone programu");
    }
}
