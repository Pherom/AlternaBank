package com.alternabank.graphical.ui.application;

import com.alternabank.engine.AlternaBankEngine;
import com.alternabank.engine.user.User;
import com.alternabank.engine.xml.event.*;
import com.alternabank.engine.xml.event.listener.*;
import com.alternabank.graphical.ui.application.admin.AdminViewController;
import com.alternabank.graphical.ui.application.header.HeaderController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class AppController implements Initializable, XMLLoadSuccessListener, XMLCustomerLoadFailureListener, XMLCategoryLoadFailureListener, XMLFileLoadFailureListener, XMLLoanLoadFailureListener {

    @FXML private BorderPane appComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private BorderPane adminViewComponent;
    @FXML private BorderPane userViewComponent;
    @FXML private AdminViewController adminViewComponentController;
    @FXML private UserViewController userViewComponentController;
    private final StringProperty loadedFilePathStringProperty = new SimpleStringProperty();
    private final StringProperty currentTimeStringProperty = new SimpleStringProperty();

    public void onUserSelection(User selectedUser) {
        if(selectedUser == AlternaBankEngine.getInstance().getAdmin())
            appComponent.setCenter(adminViewComponent);
        else appComponent.setCenter(userViewComponent);
    }

    private void updateCurrentTimeStringProperty() {
        currentTimeStringProperty.set(AlternaBankEngine.getInstance().getTimeUnit() + " " + AlternaBankEngine.getInstance().getCurrentTime());
    }

    public void onAdvanceTimeRequest(ActionEvent event) {
        AlternaBankEngine.getInstance().advanceTime();
        updateCurrentTimeStringProperty();
    }

    public void onLoadFileRequest(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File chosenFile = fileChooser.showOpenDialog(appComponent.getScene().getWindow());
        if(chosenFile != null) {
            AlternaBankEngine.getInstance().loadFromXMLFile(chosenFile.toPath());
        }
    }

    public void setHeaderController(HeaderController controller) {
        this.headerComponentController = controller;
        controller.setAppController(this);
    }

    public void setAdminViewController(AdminViewController controller) {
        this.adminViewComponentController = controller;
        controller.setAppController(this);
    }

    private void registerAsXMLLoadListener() {
        AlternaBankEngine.getInstance().addXMLLoadSuccessListener(this);
        AlternaBankEngine.getInstance().addXMLCategoryLoadFailureListener(this);
        AlternaBankEngine.getInstance().addXMLCustomerLoadFailureListener(this);
        AlternaBankEngine.getInstance().addXMLLoanLoadFailureListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerAsXMLLoadListener();
        updateCurrentTimeStringProperty();
        setHeaderController(headerComponentController);
        setAdminViewController(adminViewComponentController);
        headerComponentController.getLoadedXMLFileTextFieldStringProperty().bind(loadedFilePathStringProperty);
        headerComponentController.getCurrentTimeLabelStringProperty().bind(currentTimeStringProperty);
    }

    private void showLoadSuccessAlert(Path fileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("XML File Load Success");
        alert.setContentText(String.format("The file: %s was loaded successfully!", fileName));
        alert.showAndWait();
    }

    @Override
    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        Path loadedFilePath = event.getSource().getLastLoadedFilePath();
        loadedFilePathStringProperty.set(loadedFilePath.toString());
        adminViewComponentController.loadedSuccessfully(event);
        headerComponentController.loadedSuccessfully(event);
        showLoadSuccessAlert(loadedFilePath.getFileName());
    }

    private void showLoadFailureAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("XML File Load Failed");
        alert.setContentText(String.format("The xml file failed to load:%s%s", System.lineSeparator(), errorMessage));
        alert.showAndWait();
    }

    @Override
    public void customerLoadFailed(XMLCustomerLoadFailureEvent event) {
        showLoadFailureAlert(event.getErrorMessage());
        event.getSource().stopLoading();
    }

    @Override
    public void categoryLoadFailed(XMLCategoryLoadFailureEvent event) {
        showLoadFailureAlert(event.getErrorMessage());
        event.getSource().stopLoading();
    }

    @Override
    public void fileLoadFailed(XMLFileLoadFailureEvent event) {
        showLoadFailureAlert(event.getErrorMessage());
        event.getSource().stopLoading();
    }

    @Override
    public void loanLoadFailed(XMLLoanLoadFailureEvent event) {
        showLoadFailureAlert(event.getErrorMessage());
        event.getSource().stopLoading();
    }
}
