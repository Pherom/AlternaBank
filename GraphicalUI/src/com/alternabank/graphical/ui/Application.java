package com.alternabank.graphical.ui;

import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.AppController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class Application extends javafx.application.Application {

    private static final String APP_FXML_RESOURCE = "application/App.fxml";

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_RESOURCE);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        Scene scene = new Scene(root, 1000, 630);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
