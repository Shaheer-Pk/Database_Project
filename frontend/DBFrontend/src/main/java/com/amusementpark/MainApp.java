package com.amusementpark;

import com.amusementpark.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Application entry point.
 * Starts on the Login screen; MainController handles all navigation after that.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL login = getClass().getResource("/fxml/Login.fxml");
        URL loginstyle = getClass().getResource("/css/style.css");

        FXMLLoader loader = new FXMLLoader(login);
        Scene scene = new Scene(loader.load(), 1280, 800);
        scene.getStylesheets().add(loginstyle.toExternalForm());

        primaryStage.setTitle("Amusement Park — Admin Console");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Cleanly close DB connection on app exit
        DatabaseConnection.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
