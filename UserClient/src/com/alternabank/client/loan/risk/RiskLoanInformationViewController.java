package com.alternabank.client.loan.risk;

import com.alternabank.client.loan.LoanViewController;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        if(loanViewComponentController.getSelectedLoanDetails().getStatus() == LoanStatusData.RISK) {
            delayedInstallmentCountLabel.setText(Integer.toString(loanViewComponentController.getSelectedLoanDetails().getDelayedInstallmentCount()));
            accumulatedDebtTotalLabel.setText(String.format("%.2f", loanViewComponentController.getSelectedLoanDetails().getAccumulatedDebtTotal()));
        }
    }

}
