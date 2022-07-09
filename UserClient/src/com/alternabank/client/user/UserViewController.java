package com.alternabank.client.user;

import com.alternabank.client.app.AppController;
import com.alternabank.client.user.information.UserViewInformationController;
import com.alternabank.client.user.investment.UserViewInvestmentController;
import com.alternabank.client.user.payment.UserViewPaymentController;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.loan.LoanDetails;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.*;

public class UserViewController implements Initializable {

    @FXML private TabPane userViewComponent;
    @FXML private Tab investmentTab;
    @FXML private Tab paymentTab;
    @FXML private Tab informationTab;
    @FXML private AppController appComponentController;
    @FXML private UserViewInformationController userViewInformationComponentController;
    @FXML private UserViewInvestmentController userViewInvestmentComponentController;
    @FXML private UserViewPaymentController userViewPaymentComponentController;

    private TimerTask accountDetailsRefresher;

    private Timer timer;
    private final ListProperty<LoanDetails> loanerLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<LoanDetails> borrowerLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ObjectProperty<AccountDetails> accountDetails = new SimpleObjectProperty<>();

    public ListProperty<LoanDetails> loanerLoanDetailsProperty() {
        return loanerLoanDetails;
    }

    public ListProperty<LoanDetails> borrowerLoanDetailsProperty() {
        return borrowerLoanDetails;
    }

    public List<LoanDetails> getBorrowerLoanDetails() {
        return borrowerLoanDetails.get();
    }

    public ObjectProperty<AccountDetails> accountDetailsProperty() {
        return accountDetails;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails.get();
    }

    public double getAccountBalance() {
        return accountDetails.get() != null ? accountDetails.get().getBalance() : 0;
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

        Platform.runLater(() -> {
            loanerLoanDetails.set(new FilteredList<>(LoanDetailsUtil.loanDetails, loanDetails -> loanDetails.getInvestmentsByLenderName().containsKey(appComponentController.getUsername())));
            borrowerLoanDetails.set(new FilteredList<>(LoanDetailsUtil.loanDetails, loanDetails -> loanDetails.getBorrowerName().equals(appComponentController.getUsername())));
        });
    }

    private void updateAccountDetails(AccountDetails accountDetails) {
        Platform.runLater(() -> {
            if (!ServerTimeUtil.rewindMode.get() && (this.accountDetails.get() == null || this.accountDetails.get().getLedgerVersion() != accountDetails.getLedgerVersion()))
                accountDetails.getTransactionRecords().forEach(transactionRecord -> Platform.runLater(() -> Notifications.create().text(transactionRecord.toString()).showInformation()));
            this.accountDetails.set(accountDetails);
        });
    }

    private void startAccountDetailsRefresher() {
        accountDetailsRefresher = new AccountDetailsRefresher(
                this::updateAccountDetails);
        timer = new Timer();
        timer.schedule(accountDetailsRefresher, 500, 500);
    }

    public void startRefreshers() {
        startAccountDetailsRefresher();
        userViewPaymentComponentController.startRefreshers();
        userViewInformationComponentController.startRefreshers();
    }
}
