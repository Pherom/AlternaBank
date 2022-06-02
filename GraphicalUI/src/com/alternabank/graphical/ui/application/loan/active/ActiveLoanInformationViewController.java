package com.alternabank.graphical.ui.application.loan.active;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.account.AccountLedgerListViewController;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ActiveLoanInformationViewController {

    @FXML private Label activeSinceLabel;

    @FXML private Label nextInstallmentLabel;

    @FXML private Label paidPrincipalLabel;

    @FXML private Label paidInterestLabel;

    @FXML private Label remainingInterestLabel;

    @FXML private Label remainingPrincipalLabel;

    @FXML private AccountLedgerListViewController accountLedgerListViewComponentController;

    private LoanViewController loanViewComponentController;

    public void setLoanViewController(LoanViewController controller) {
        this.loanViewComponentController = controller;
    }

    public void onLoanSelected() {
        if (loanViewComponentController.getSelectedLoanDetails().getStatus() != Loan.Status.PENDING) {
            String timeUnitName = UserManager.getInstance().getAdmin().getTimeManager().getTimeUnitName();
            LoanDetails selectedLoanDetails = loanViewComponentController.getSelectedLoanDetails();
            activeSinceLabel.setText(String.format("%s %d", timeUnitName, selectedLoanDetails.getStatusTimes().get(Loan.Status.ACTIVE)));
            Optional<Integer> nextInstallmentTime = selectedLoanDetails.getNextInstallmentTime();
            nextInstallmentLabel.setText(nextInstallmentTime.isPresent() ? String.format("%s %d", timeUnitName, nextInstallmentTime.get()) : "N/A");
            paidPrincipalLabel.setText(String.format("%.2f", selectedLoanDetails.getPaidPrincipal()));
            paidInterestLabel.setText(String.format("%.2f", selectedLoanDetails.getPaidInterest()));
            remainingInterestLabel.setText(String.format("%.2f", selectedLoanDetails.getRemainingInterest()));
            remainingPrincipalLabel.setText(String.format("%.2f", selectedLoanDetails.getRemainingPrincipal()));
            List<Transaction.Record> transactionRecords = new ArrayList<>(selectedLoanDetails.getAccountDetails().getTransactionRecords());
            Collections.reverse(transactionRecords);
            accountLedgerListViewComponentController.setTransactionRecords(transactionRecords);
        }
    }

}
