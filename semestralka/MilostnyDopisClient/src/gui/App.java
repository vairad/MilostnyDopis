package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private Stage stage;
    ResourceBundle labels;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

       // Parent root = FXMLLoader.load(getClass().getResource("startScreen.fxml"));

      //  Scene scene = new Scene(root, 300, 500);
      //  scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());

        loadView(new Locale("cs", "CZ"));

        //primaryStage.setTitle(Texts.TITLE);
      //  primaryStage.setScene(scene);
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


        System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

        launch(args);

    }
}
