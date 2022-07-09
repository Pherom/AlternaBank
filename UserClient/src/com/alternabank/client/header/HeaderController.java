package com.alternabank.client.header;

import com.alternabank.client.app.AppController;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    private AppController appComponentController;

    @FXML private Label currentTimeLabel;
    @FXML private TextField lastLoadedXMLFileTextField;

    @FXML private Button loadButton;

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
       currentTimeLabel.textProperty().bind(new StringBinding() {

           { super.bind(currentTimeLabel.textProperty(), ServerTimeUtil.currentTime, ServerTimeUtil.timeUnitName, ServerTimeUtil.rewindMode);}

           @Override
           protected String computeValue() {
               return String.format("%s %d %s", ServerTimeUtil.timeUnitName.get(), ServerTimeUtil.currentTime.get(), ServerTimeUtil.isRewindMode() ? "(REWIND)" : "");
           }
       });
    }

    private void showXMLFileLoadSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("Success!");
        alert.setContentText("XML file loaded successfully!");
        alert.showAndWait();
    }

    private void showXMLFileLoadFailureAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Error!");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    @FXML
    private void xmlFileLoadRequested(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File chosenFile = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if(chosenFile != null) {
            String finalUrl = HttpUrl
                    .parse("http://localhost:8080/AlternaBank/load-xml")
                    .newBuilder()
                    .build()
                    .toString();
            RequestBody requestBody = new MultipartBody.Builder().addFormDataPart("file", chosenFile.getName(), RequestBody.create(chosenFile, MediaType.parse("text/plain"))).build();
            try {
                Response response = HttpClientUtil.runPostSync(finalUrl, requestBody);
                if (response.code() == 200) {
                    lastLoadedXMLFileTextField.setText(chosenFile.getAbsolutePath());
                    showXMLFileLoadSuccessAlert();
                }
                else {
                    showXMLFileLoadFailureAlert(response.body().string());
                }
            } catch (IOException e) {
                showXMLFileLoadFailureAlert("Failed to load xml file");
            }

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadButton.disableProperty().bind(ServerTimeUtil.rewindMode);
    }
}
