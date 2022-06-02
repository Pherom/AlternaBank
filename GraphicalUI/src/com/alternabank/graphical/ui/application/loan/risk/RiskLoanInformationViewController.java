package com.alternabank.graphical.ui.application.loan.risk;

import com.alternabank.engine.loan.Loan;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class RiskLoanInformationViewController {

    @FXML
    private Label delayedInstallmentCountLabel;

    @FXML
    private Label accumulatedDebtTotalLabel;

    private LoanViewController loanViewComponentController;

    public void setLoanViewController(LoanViewController controller) {
        this.loanViewComponentController = controller;
    }

    public void onLoanSelected() {
        if(loanViewComponentController.getSelectedLoanDetails().getStatus() == Loan.Status.RISK) {
            delayedInstallmentCountLabel.setText(Integer.toString(loanViewComponentController.getSelectedLoanDetails().getDelayedInstallmentCount()));
            accumulatedDebtTotalLabel.setText(String.format("%.2f", loanViewComponentController.getSelectedLoanDetails().getAccumulatedDebtTotal()));
        }
    }

}
