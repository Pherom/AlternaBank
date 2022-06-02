package com.alternabank.graphical.ui.application.loan.finished;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class FinishedLoanInformationViewController {

    @FXML private Label activeSinceLabel;

    @FXML private Label endedOnLabel;

    private LoanViewController loanViewComponentController;

    public void setLoanViewController(LoanViewController controller) {
        this.loanViewComponentController = controller;
    }

    public void onLoanSelected() {
        if (loanViewComponentController.getSelectedLoanDetails().getStatus() == Loan.Status.FINISHED) {
            String timeUnitName = UserManager.getInstance().getAdmin().getTimeManager().getTimeUnitName();
            activeSinceLabel.setText(String.format("%s %d", timeUnitName, loanViewComponentController.getSelectedLoanDetails().getStatusTimes().get(Loan.Status.ACTIVE)));
            endedOnLabel.setText(String.format("%s %d", timeUnitName, loanViewComponentController.getSelectedLoanDetails().getStatusTimes().get(Loan.Status.FINISHED)));
        }
    }

}
