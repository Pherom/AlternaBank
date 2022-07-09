package com.alternabank.admin.client.header;

import com.alternabank.admin.client.app.AppController;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import okhttp3.*;

import java.io.IOException;

public class HeaderController {

    private AppController appComponentController;

    @FXML private Label currentTimeLabel;

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

    private void showTimeManipulationErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Failed to process time manipulation request");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onAdvanceTimeRequest(ActionEvent event) throws IOException {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/advance-time")
                .newBuilder().build().toString();
        Response response = HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
        if (response.code() != 200) {
            showTimeManipulationErrorAlert("Advance time request failed");
        }
    }

    @FXML
    private void onReverseTimeRequest(ActionEvent event) throws IOException {
        String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/reverse-time")
                .newBuilder().build().toString();
        Response response = HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
        if (response.code() != 200) {
            showTimeManipulationErrorAlert("Reverse time request failed");
        }
    }
}
