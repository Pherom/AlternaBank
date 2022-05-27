package com.alternabank.graphical.ui.application;

import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;
import com.alternabank.engine.loan.event.listener.LoanStatusUpdateListener;
import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.XMLLoader;
import com.alternabank.engine.xml.event.*;
import com.alternabank.engine.xml.event.listener.*;
import com.alternabank.graphical.ui.application.admin.AdminViewController;
import com.alternabank.graphical.ui.application.header.HeaderController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppController implements Initializable, XMLLoadSuccessListener, XMLCustomerLoadFailureListener, XMLCategoryLoadFailureListener, XMLFileLoadFailureListener, XMLLoanLoadFailureListener, TimeAdvancementListener, LoanStatusUpdateListener, UnilateralTransactionListener, BilateralTransactionListener {

    @FXML private BorderPane appComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private BorderPane adminViewComponent;
    @FXML private TabPane userViewComponent;
    @FXML private AdminViewController adminViewComponentController;
    @FXML private UserViewController userViewComponentController;
    private final StringProperty loadedFilePathStringProperty = new SimpleStringProperty();
    private final StringProperty currentTimeStringProperty = new SimpleStringProperty();
    private final ListProperty<CustomerDetails> customerDetails = new SimpleListProperty<CustomerDetails>();
    private final ListProperty<LoanDetails> loanDetails = new SimpleListProperty<LoanDetails>();
    private final ObjectProperty<User> selectedUser = new SimpleObjectProperty<>();
    private final ListProperty<User> availableUsers = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ListProperty<String> availableLoanCategories = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ListProperty<LoanDetails> loanDetailsProperty() {
        return loanDetails;
    }

    public ListProperty<CustomerDetails> customerDetailsProperty() {
        return customerDetails;
    }

    public ObjectProperty<User> selectedUserProperty() {
        return selectedUser;
    }

    public ListProperty<User> availableUsersProperty() {
        return availableUsers;
    }

    public User getSelectedUser() {
        return selectedUser.getValue();
    }

    public void setSelectedUser(User user) {
        selectedUser.set(user);
    }

    public ListProperty<String> availableLoanCategoriesProperty() {
        return availableLoanCategories;
    }

    private void onUserSelection(User selectedUser) {
        UserManager.getInstance().setCurrentUser(selectedUser);
        if(selectedUser == UserManager.getInstance().getAdmin()) {
            refreshCustomerAndLoanDetails();
            userViewComponent.setVisible(false);
            adminViewComponent.setVisible(true);
        }
        else
        {
            adminViewComponent.setVisible(false);
            userViewComponent.setVisible(true);
        }
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

    private void registerAsTransactionListener() {
        UserManager.getInstance().getAdmin().getTransactionManager().addUnilateralTransactionListener(this);
        UserManager.getInstance().getAdmin().getTransactionManager().addBilateralTransactionListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerAsXMLLoadListener();
        registerAsTimeAdvancementListener();
        registerAsLoanStatusUpdateListener();
        registerAsTransactionListener();
        updateCurrentTimeStringProperty(UserManager.getInstance().getAdmin().getTimeManager().getCurrentTime());
        headerComponentController.setAppController(this);
        adminViewComponentController.setAppController(this);
        userViewComponentController.setAppController(this);
        headerComponentController.getLoadedXMLFileTextFieldStringProperty().bind(loadedFilePathStringProperty);
        headerComponentController.getCurrentTimeLabelStringProperty().bind(currentTimeStringProperty);

        availableUsers.add(UserManager.getInstance().getAdmin());
        selectedUser.set(UserManager.getInstance().getAdmin());
        selectedUser.addListener((observable, oldValue, newValue) -> onUserSelection(newValue));
    }

    private void showLoadSuccessAlert(Path fileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("XML File Load Success");
        alert.setContentText(String.format("The file: %s was loaded successfully!", fileName));
        alert.showAndWait();
    }

    public void refreshCustomerAndLoanDetails() {
        customerDetails.set(FXCollections.observableList(new ArrayList<>(UserManager.getInstance().getAdmin().getCustomerManager().getCustomerDetails())));
        loanDetails.set(FXCollections.observableList(new ArrayList<>(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails())));
    }

    public void refreshAvailableLoanCategories() {
        availableLoanCategories.set(FXCollections.observableList(new ArrayList<>(UserManager.getInstance().getAdmin().getLoanManager().getAvailableCategories())));
    }

    @Override
    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        Path loadedFilePath = event.getSource().getLastLoadedFilePath();
        loadedFilePathStringProperty.set(loadedFilePath.toString());
        refreshCustomerAndLoanDetails();
        refreshAvailableLoanCategories();
        availableUsers.removeIf(user -> user != event.getSource().getAdmin());
        availableUsers.addAll(UserManager.getInstance().getUsers());
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

    @Override
    public void timeAdvanced(TimeAdvancementEvent event) {
        updateCurrentTimeStringProperty(event.getTimeAfter());
    }

    @Override
    public void statusUpdated(LoanStatusUpdateEvent event) {

    }

    @Override
    public void unilateralTransactionExecuted(UnilateralTransactionEvent event) {
        userViewComponentController.unilateralTransactionExecuted(event);
    }

    @Override
    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        userViewComponentController.bilateralTransactionExecuted(event);
    }
}
