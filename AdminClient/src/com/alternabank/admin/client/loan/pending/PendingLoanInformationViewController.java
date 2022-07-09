package com.alternabank.admin.client.loan.pending;

import com.alternabank.admin.client.loan.LoanViewController;
import com.alternabank.admin.client.loan.investment.InvestmentInformationViewController;
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
