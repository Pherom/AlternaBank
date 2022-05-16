package com.alternabank.graphical.ui.application;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;
import com.alternabank.engine.loan.event.listener.LoanStatusUpdateListener;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.XMLLoader;
import com.alternabank.engine.xml.event.*;
import com.alternabank.engine.xml.event.listener.*;
import com.alternabank.graphical.ui.application.admin.AdminViewController;
import com.alternabank.graphical.ui.application.header.HeaderController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class AppController implements Initializable, XMLLoadSuccessListener, XMLCustomerLoadFailureListener, XMLCategoryLoadFailureListener, XMLFileLoadFailureListener, XMLLoanLoadFailureListener, TimeAdvancementListener, LoanStatusUpdateListener {

    @FXML private BorderPane appComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private BorderPane adminViewComponent;
    @FXML private TabPane userViewComponent;
    @FXML private AdminViewController adminViewComponentController;
    @FXML private UserViewController userViewComponentController;
    private final StringProperty loadedFilePathStringProperty = new SimpleStringProperty();
    private final StringProperty currentTimeStringProperty = new SimpleStringProperty();

    public void onUserSelection(User selectedUser) {
        if(selectedUser == UserManager.getInstance().getAdmin()) {
            userViewComponent.setVisible(false);
            adminViewComponent.setVisible(true);
        }
        else
        {
            adminViewComponent.setVisible(false);
            userViewComponentController.populate(selectedUser);
            userViewComponent.setVisible(true);
        }
        UserManager.getInstance().setCurrentUser(selectedUser);
    }

    private void updateCurrentTimeStringProperty(int newTime) {
        String timeUnitName = UserManager.getInstance().getAdmin().getTimeManager().getTimeUnitName();
        currentTimeStringProperty.set(timeUnitName + " " + newTime);
    }

    public void onAdvanceTimeRequest(ActionEvent event) {
        UserManager.getInstance().getAdmin().advanceTime();
    }

    public void onLoadFileRequest(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File chosenFile = fileChooser.showOpenDialog(appComponent.getScene().getWindow());
        if(chosenFile != null) {
            UserManager.getInstance().getAdmin().getXmlFileLoader().loadSystemFromFile(chosenFile.toPath());
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

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        controller.setAppController(this);
    }

    private void registerAsXMLLoadListener() {
        XMLLoader xmlFileLoader = UserManager.getInstance().getAdmin().getXmlFileLoader();
        xmlFileLoader.addLoadSuccessListener(this);
        xmlFileLoader.addCategoryLoadFailureListener(this);
        xmlFileLoader.addCustomerLoadFailureListener(this);
        xmlFileLoader.addLoanLoadFailureListener(this);
    }

    private void registerAsTimeAdvancementListener() {
        UserManager.getInstance().getAdmin().getTimeManager().addTimeAdvancementListener(this);
    }

    private void registerAsLoanStatusUpdateListener() {
        UserManager.getInstance().getAdmin().getLoanManager().addLoanStatusUpdateListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerAsXMLLoadListener();
        registerAsTimeAdvancementListener();
        registerAsLoanStatusUpdateListener();
        updateCurrentTimeStringProperty(UserManager.getInstance().getAdmin().getTimeManager().getCurrentTime());
        setHeaderController(headerComponentController);
        setAdminViewController(adminViewComponentController);
        setUserViewController(userViewComponentController);
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
        adminViewComponentController.populateLoanView(event.getSource().getAdmin().getLoanManager().getLoanDetails());
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

    @Override
    public void timeAdvanced(TimeAdvancementEvent event) {
        updateCurrentTimeStringProperty(event.getTimeAfter());
    }

    @Override
    public void statusUpdated(LoanStatusUpdateEvent event) {
        adminViewComponentController.populateLoanView(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails());
    }
}
