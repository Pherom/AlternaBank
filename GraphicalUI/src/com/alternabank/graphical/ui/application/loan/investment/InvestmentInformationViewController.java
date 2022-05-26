package com.alternabank.graphical.ui.application.loan.investment;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.investment.lender.LenderInformationViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InvestmentInformationViewController {

    @FXML private Label totalInvestmentLabel;

    @FXML private Label remainingInvestmentLabel;

    @FXML private LenderInformationViewController lenderInformationViewComponentController;

    public void populate(LoanDetails loanDetails) {
        totalInvestmentLabel.setText(String.format("%.2f", loanDetails.getTotalInvestment()));
        remainingInvestmentLabel.setText(String.format("%.2f", loanDetails.getRemainingInvestment()));
        lenderInformationViewComponentController.populate(loanDetails);
    }

}
