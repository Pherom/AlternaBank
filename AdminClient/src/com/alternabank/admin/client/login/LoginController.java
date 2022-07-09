package com.alternabank.admin.client.login;

import com.alternabank.admin.client.app.AppController;
import com.alternabank.client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LoginController {

    private AppController appComponentController;

    private Scene appScene;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    public void setAppComponentController(AppController controller) {
        this.appComponentController = controller;
    }

    public void setAppScene(Scene appScene) {
        this.appScene = appScene;
    }

    private void showLoginSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("Login successful!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoginErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Login attempt failed!");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    @FXML
    private void loginRequested(ActionEvent event) {
        String username = usernameTextField.getText();

        if (username.isEmpty())
            showLoginErrorAlert(String.format("Username cannot be empty.%sPlease try again...", System.lineSeparator()));
        else {
            String finalUrl = HttpUrl
                    .parse("http://localhost:8080/AlternaBank/login")
                    .newBuilder()
                    .addQueryParameter("username", username)
                    .addQueryParameter("admin", "true")
                    .build()
                    .toString();

            HttpClientUtil.runGetAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                                showLoginErrorAlert(e.getMessage());
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                                    showLoginErrorAlert(responseBody);
                                }
                        );
                    } else {
                        Platform.runLater(() -> {
                            appComponentController.setUsername(username);
                            showLoginSuccessAlert("Successfully logged in!");
                            Stage primaryStage = ((Stage)((Node)event.getSource()).getScene().getWindow());
                            primaryStage.setScene(appScene);
                            primaryStage.centerOnScreen();
                        });
                    }
                }
            });
        }
    }

}


/*package com.alternabank.client.login;

import com.alternabank.httpclient.util.HttpClientUtil;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;

public class LoginController {

    private StringProperty username = new SimpleStringProperty();
    private BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

    private void showLoginSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("Login successful!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoginErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Login attempt failed!");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private String showLoginForm() {
        TextInputDialog loginDialog = new TextInputDialog();
        loginDialog.setHeaderText("Welcome to AlternaBank!");
        loginDialog.setContentText("Username:");
        loginDialog.showAndWait();
        return loginDialog.getResult();
    }

    private void requestUsername() {
        do {
            username.set(showLoginForm());
            if (username.get().isEmpty())
                showLoginErrorAlert(String.format("Username cannot be empty.%sPlease try again...", System.lineSeparator()));
        } while (username.get() == null || username.get().isEmpty());
    }

    public void requestLogin() {
        while (loginSuccess.get() == false) {
            requestUsername();

            String finalUrl = HttpUrl
                    .parse("http://localhost:8080/AlternaBank/login")
                    .newBuilder()
                    .addQueryParameter("username", username.get())
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                                showLoginErrorAlert(e.getMessage());
                                Platform.exit();
                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                                    showLoginErrorAlert(responseBody);
                                }
                        );
                    } else {
                        Platform.runLater(() -> {
                            loginSuccess.set(true);
                            showLoginSuccessAlert("Successfully logged in!");
                        });
                    }
                }
            });
        }
    }

}*/
