package com.alternabank.client;

import com.alternabank.client.app.AppController;
import com.alternabank.client.login.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Application extends javafx.application.Application {

    private static final String LOGIN_FXML_RESOURCE = "login/Login.fxml";
    private static final String APP_FXML_RESOURCE = "app/App.fxml";

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loginFXMLLoader = new FXMLLoader();
        URL url = getClass().getResource(LOGIN_FXML_RESOURCE);
        loginFXMLLoader.setLocation(url);
        Parent login = loginFXMLLoader.load(url.openStream());
        LoginController loginController = loginFXMLLoader.getController();

        FXMLLoader appFXMLLoader = new FXMLLoader();
        url = getClass().getResource(APP_FXML_RESOURCE);
        appFXMLLoader.setLocation(url);
        Parent app = appFXMLLoader.load(url.openStream());
        AppController appController = appFXMLLoader.getController();
        loginController.setAppComponentController(appController);

        Scene loginScene = new Scene(login, 350, 200);
        Scene appScene = new Scene(app, 1200, 800);
        loginController.setAppScene(appScene);

        primaryStage.centerOnScreen();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}
