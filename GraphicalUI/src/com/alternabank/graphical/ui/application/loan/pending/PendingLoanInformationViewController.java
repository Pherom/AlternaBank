package com.alternabank.graphical.ui.application.loan.pending;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import com.alternabank.graphical.ui.application.loan.investment.InvestmentInformationViewController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class PendingLoanInformationViewController implements Initializable {

    @FXML private InvestmentInformationViewController investmentInformationViewComponentController;

    private LoanViewController loanViewComponentController;

    public void setLoanViewController(LoanViewController controller) {
        this.loanViewComponentController = controller;
    }

    public LoanViewController getLoanViewController() {
        return loanViewComponentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        investmentInformationViewComponentController.setPendingLoanInformationViewController(this);
    }

    public void onLoanSelected() {
        investmentInformationViewComponentController.onLoanSelected();
    }

    public void onLoanDeselected() {
        investmentInformationViewComponentController.onLoanDeselected();
    }
}
