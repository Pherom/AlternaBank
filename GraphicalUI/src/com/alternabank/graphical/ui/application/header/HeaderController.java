package com.alternabank.graphical.ui.application.header;

import com.alternabank.engine.AlternaBankEngine;
import com.alternabank.engine.user.User;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.engine.xml.event.listener.XMLLoadSuccessListener;
import com.alternabank.graphical.ui.application.AppController;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HeaderController implements Initializable {

    @FXML private AppController appController;
    @FXML private Label currentTimeLabel;
    @FXML private TextField loadedXMLFileTextField;
    @FXML private ChoiceBox<String> userSelectionChoiceBox;

    public StringProperty getCurrentTimeLabelStringProperty() {
        return currentTimeLabel.textProperty();
    }

    public StringProperty getLoadedXMLFileTextFieldStringProperty() {
        return loadedXMLFileTextField.textProperty();
    }

    public void setAppController(AppController controller) {
        this.appController = controller;
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        userSelectionChoiceBox.getItems().remove(1, userSelectionChoiceBox.getItems().size());
        AlternaBankEngine.getInstance().getUsers().stream().map(User::getName).forEach(name -> userSelectionChoiceBox.getItems().add(name));
        userSelectionChoiceBox.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String adminName = AlternaBankEngine.getInstance().getAdmin().getName();
        userSelectionChoiceBox.getItems().add(adminName);
        userSelectionChoiceBox.setValue(adminName);
        userSelectionChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean adminSelected = userSelectionChoiceBox.getSelectionModel().isSelected(0);
            appController.onUserSelection(adminSelected ? AlternaBankEngine.getInstance().getAdmin() : AlternaBankEngine.getInstance().getUser(newValue));
        });
    }
}
