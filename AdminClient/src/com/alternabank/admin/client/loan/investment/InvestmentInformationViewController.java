package com.alternabank.admin.client.loan.investment;

import com.alternabank.admin.client.loan.investment.lender.LenderInformationViewController;
import com.alternabank.admin.client.loan.pending.PendingLoanInformationViewController;
import com.alternabank.dto.loan.LoanDetails;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class InvestmentInformationViewController implements Initializable {

    @FXML private Label totalInvestmentLabel;

    @FXML private Label remainingInvestmentLabel;

    @FXML private LenderInformationViewController lenderInformationViewComponentController;

    private PendingLoanInformationViewController pendingLoanInformationViewComponentController;

    public void onLoanSelected() {
        LoanDetails selectedLoanDetails = pendingLoanInformationViewComponentController.getLoanViewController().getSelectedLoanDetails();
        totalInvestmentLabel.setText(String.format("%.2f", selectedLoanDetails.getTotalInvestment()));
        remainingInvestmentLabel.setText(String.format("%.2f", selectedLoanDetails.getRemainingInvestment()));
        lenderInformationViewComponentController.onLoanSelected();
    }

    public void onLoanDeselected() {
        totalInvestmentLabel.setText("#");
        remainingInvestmentLabel.setText("#");
        lenderInformationViewComponentController.onLoanDeselected();
    }

    public void setPendingLoanInformationViewController(PendingLoanInformationViewController controller) {
        this.pendingLoanInformationViewComponentController = controller;
    }

    public PendingLoanInformationViewController getPendingLoanInformationViewController() {
        return pendingLoanInformationViewComponentController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lenderInformationViewComponentController.setInvestmentInformationViewComponentController(this);
    }
}
