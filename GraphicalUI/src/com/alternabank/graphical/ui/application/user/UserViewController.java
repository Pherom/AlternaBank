package com.alternabank.graphical.ui.application.user;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.event.PaymentDueEvent;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.graphical.ui.application.AppController;
import com.alternabank.graphical.ui.application.user.information.UserViewInformationController;
import com.alternabank.graphical.ui.application.user.investment.UserViewInvestmentController;
import com.alternabank.graphical.ui.application.user.payment.UserViewPaymentController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

public class UserViewController implements Initializable {

    @FXML private TabPane userViewComponent;
    @FXML private Tab investmentTab;
    @FXML private Tab paymentTab;
    @FXML private Tab informationTab;
    @FXML private AppController appComponentController;
    @FXML private UserViewInformationController userViewInformationComponentController;
    @FXML private UserViewInvestmentController userViewInvestmentComponentController;
    @FXML private UserViewPaymentController userViewPaymentComponentController;
    private final ListProperty<LoanDetails> loanerLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<LoanDetails> borrowerLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Transaction.Record> accountLedger = new SimpleListProperty<>();
    private final DoubleProperty accountBalance = new SimpleDoubleProperty();

    public ListProperty<LoanDetails> loanerLoanDetailsProperty() {
        return loanerLoanDetails;
    }

    public ListProperty<LoanDetails> borrowerLoanDetailsProperty() {
        return borrowerLoanDetails;
    }

    public List<LoanDetails> getBorrowerLoanDetails() {
        return borrowerLoanDetails.get();
    }

    public ListProperty<Transaction.Record> accountLedgerProperty() {
        return accountLedger;
    }

    public DoubleProperty accountBalanceProperty() {
        return accountBalance;
    }

    public double getAccountBalance() {
        return accountBalance.get();
    }

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
    }

    public AppController getAppController() {
        return appComponentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userViewInformationComponentController.setUserViewController(this);
        userViewInvestmentComponentController.setUserViewController(this);
        userViewPaymentComponentController.setUserViewController(this);
    }

    private void refreshAccountBalance(User selectedUser) {
        accountBalance.set(UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(selectedUser.getName()).toCustomerDetails().getAccountDetails().getBalance());
    }

    public void onUserSelection() {
        if (appComponentController.getSelectedUser() != UserManager.getInstance().getAdmin()) {
            loanerLoanDetails.set(new FilteredList<LoanDetails>(appComponentController.loanDetailsProperty(), loanDetails -> loanDetails.getInvestmentByLenderName().containsKey(appComponentController.getSelectedUser().getName())));
            borrowerLoanDetails.set(new FilteredList<LoanDetails>(appComponentController.loanDetailsProperty(), loanDetails -> loanDetails.getBorrowerName().equals(appComponentController.getSelectedUser().getName())));
            List<Transaction.Record> transactionRecordList = new LinkedList<>(UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(appComponentController.getSelectedUser().getName()).toCustomerDetails().getAccountDetails().getTransactionRecords());
            Collections.reverse(transactionRecordList);
            accountLedger.set(FXCollections.observableList(transactionRecordList));
            refreshAccountBalance(appComponentController.getSelectedUser());
            userViewInvestmentComponentController.prepareForNewInvestmentRequest();
            userViewPaymentComponentController.onUserSelection();
        }
    }

    public void unilateralTransactionExecuted(UnilateralTransactionEvent event) {
        if(appComponentController.selectedUserProperty().get().getName().equals(event.getRecord().getInitiatorID())) {
            accountLedger.add(0, event.getRecord());
            refreshAccountBalance(appComponentController.getSelectedUser());
            Notifications.create().text(event.getRecord().toString()).showInformation();
        }
    }

    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        if(appComponentController.getSelectedUser().getName().equals(event.getRecord().getInitiatorID())
        || appComponentController.getSelectedUser().getName().equals(event.getRecord().getRecipientID())) {
            accountLedger.add(0, event.getRecord());
            refreshAccountBalance(appComponentController.getSelectedUser());
            Notifications.create().text(event.getRecord().toString()).showInformation();
        }
    }

    public void paymentDue(PaymentDueEvent event) {
        boolean isSelectedUserBorrower = event.getLoanDetails().getBorrowerName().equals(appComponentController.getSelectedUser().getName());
        boolean isSelectedUserLender = event.getLoanDetails().getInvestmentByLenderName().containsKey(appComponentController.getSelectedUser().getName());
        if (isSelectedUserBorrower || isSelectedUserLender)
            Notifications.create().text(event.getNotification().toString()).showInformation();
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        userViewInvestmentComponentController.loadedSuccessfully(event);
    }
}
