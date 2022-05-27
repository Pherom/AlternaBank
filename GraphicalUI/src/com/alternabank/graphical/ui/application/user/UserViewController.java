package com.alternabank.graphical.ui.application.user;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
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
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UserViewController implements Initializable {

    @FXML private TabPane userViewComponent;
    @FXML private Tab investmentTab;
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
        controller.selectedUserProperty().addListener((observable, oldValue, newValue) -> onUserSelection(newValue));
    }

    public AppController getAppController() {
        return appComponentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userViewInformationComponentController.setUserViewController(this);
        userViewInvestmentComponentController.setUserViewController(this);
        userViewComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == investmentTab) {
                Optional<ButtonType> result = userViewInvestmentComponentController.showQuitConfirmationDialog();
                result.ifPresent(buttonType -> {
                    if(buttonType == ButtonType.NO)
                        userViewComponent.getSelectionModel().select(investmentTab);
                    else userViewInvestmentComponentController.prepareForNewInvestmentRequest();
                });
            }
        });
    }

    private void refreshAccountBalance(User selectedUser) {
        accountBalance.set(UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(selectedUser.getName()).toCustomerDetails().getAccountDetails().getBalance());
    }

    private void onUserSelection(User selectedUser) {
        if (selectedUser != UserManager.getInstance().getAdmin()) {
            loanerLoanDetails.set(FXCollections.observableList(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails().stream().filter(loanDetails -> loanDetails.getInvestmentByLenderName().containsKey(selectedUser.getName())).collect(Collectors.toList())));
            borrowerLoanDetails.set(FXCollections.observableList(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails().stream().filter(loanDetails -> loanDetails.getBorrowerName().equals(selectedUser.getName())).collect(Collectors.toList())));
            List<Transaction.Record> transactionRecordList = new LinkedList<>(UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(selectedUser.getName()).toCustomerDetails().getAccountDetails().getTransactionRecords());
            Collections.reverse(transactionRecordList);
            accountLedger.set(FXCollections.observableList(transactionRecordList));
            refreshAccountBalance(selectedUser);
            userViewInvestmentComponentController.prepareForNewInvestmentRequest();
        }
    }

    public void unilateralTransactionExecuted(UnilateralTransactionEvent event) {
        if(appComponentController.selectedUserProperty().get().getName().equals(event.getRecord().getInitiatorID())) {
            accountLedger.add(0, event.getRecord());
            refreshAccountBalance(appComponentController.getSelectedUser());
        }
    }

    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        if(appComponentController.selectedUserProperty().get().getName().equals(event.getRecord().getInitiatorID())
        || appComponentController.selectedUserProperty().get().getName().equals(event.getRecord().getRecipientID())) {
            accountLedger.add(0, event.getRecord());
            refreshAccountBalance(appComponentController.getSelectedUser());
        }
    }
}
