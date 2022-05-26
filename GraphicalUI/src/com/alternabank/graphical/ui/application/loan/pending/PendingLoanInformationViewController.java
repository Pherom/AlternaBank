package com.alternabank.graphical.ui.application.loan.pending;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.investment.InvestmentInformationViewController;
import javafx.fxml.FXML;

public class PendingLoanInformationViewController {

    @FXML private InvestmentInformationViewController investmentInformationViewComponentController;

    public void populate(LoanDetails loanDetails) {
        investmentInformationViewComponentController.populate(loanDetails);
    }

}
