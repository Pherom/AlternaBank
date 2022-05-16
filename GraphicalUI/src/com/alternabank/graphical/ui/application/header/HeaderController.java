package com.alternabank.graphical.ui.application.header;

import com.alternabank.engine.user.Admin;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.graphical.ui.application.AppController;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML private AppController appController;
    @FXML private Label currentTimeLabel;
    @FXML private TextField loadedXMLFileTextField;
    @FXML private ComboBox<User> userSelectionComboBox;

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
        userSelectionComboBox.getItems().remove(1, userSelectionComboBox.getItems().size());
        userSelectionComboBox.getItems().addAll(UserManager.getInstance().getUsers());
        userSelectionComboBox.setDisable(false);
    }

    private void setUserSelectionComboBoxConverter() {
        userSelectionComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user.getName();
            }

            @Override
            public User fromString(String string) {
                Admin admin = UserManager.getInstance().getAdmin();
                return string.equals(admin.getName()) ? admin : UserManager.getInstance().getUser(string);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUserSelectionComboBoxConverter();
        User admin = UserManager.getInstance().getAdmin();
        userSelectionComboBox.getItems().add(admin);
        userSelectionComboBox.setValue(admin);
        userSelectionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean adminSelected = userSelectionComboBox.getSelectionModel().isSelected(0);
            appController.onUserSelection(adminSelected ? UserManager.getInstance().getAdmin() : newValue);
        });
    }
}
