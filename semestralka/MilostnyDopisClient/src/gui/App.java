package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("startScreen.fxml"));

        primaryStage.setTitle(Texts.TITLE);
        primaryStage.setScene(new Scene(root, 300, 500));
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(500);
        primaryStage.show();
    }


    public static void main(String[] args) {


        System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

        launch(args);

    }
}
