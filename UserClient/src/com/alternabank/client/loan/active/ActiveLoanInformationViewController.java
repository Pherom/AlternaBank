package com.alternabank.client.loan.active;

import com.alternabank.client.account.AccountLedgerListViewController;
import com.alternabank.client.loan.LoanViewController;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.dto.transaction.TransactionRecord;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        if (loanViewComponentController.getSelectedLoanDetails().getStatus() != LoanStatusData.PENDING) {
            String timeUnitName = ServerTimeUtil.timeUnitName.get();
            LoanDetails selectedLoanDetails = loanViewComponentController.getSelectedLoanDetails();
            activeSinceLabel.setText(String.format("%s %d", timeUnitName, selectedLoanDetails.getStatusTimes().get(LoanStatusData.ACTIVE)));
            Optional<Integer> nextInstallmentTime = selectedLoanDetails.getNextInstallmentTime();
            nextInstallmentLabel.setText(nextInstallmentTime.isPresent() ? String.format("%s %d", timeUnitName, nextInstallmentTime.get()) : "N/A");
            paidPrincipalLabel.setText(String.format("%.2f", selectedLoanDetails.getPaidPrincipal()));
            paidInterestLabel.setText(String.format("%.2f", selectedLoanDetails.getPaidInterest()));
            remainingInterestLabel.setText(String.format("%.2f", selectedLoanDetails.getRemainingInterest()));
            remainingPrincipalLabel.setText(String.format("%.2f", selectedLoanDetails.getRemainingPrincipal()));
            List<TransactionRecord> transactionRecords = new ArrayList<>(selectedLoanDetails.getAccountDetails().getTransactionRecords());
            Collections.reverse(transactionRecords);
            accountLedgerListViewComponentController.setTransactionRecords(transactionRecords);
        }
    }

}
