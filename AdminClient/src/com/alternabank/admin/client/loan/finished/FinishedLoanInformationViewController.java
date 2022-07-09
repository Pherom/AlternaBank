package com.alternabank.admin.client.loan.finished;

import com.alternabank.admin.client.loan.LoanViewController;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FinishedLoanInformationViewController {

    @FXML private Label activeSinceLabel;

    @FXML private Label endedOnLabel;

    private LoanViewController loanViewComponentController;

    public void setLoanViewController(LoanViewController controller) {
        this.loanViewComponentController = controller;
    }

    public void onLoanSelected() {
        if (loanViewComponentController.getSelectedLoanDetails().getStatus() == LoanStatusData.FINISHED) {
            String timeUnitName = ServerTimeUtil.timeUnitName.get();
            activeSinceLabel.setText(String.format("%s %d", timeUnitName, loanViewComponentController.getSelectedLoanDetails().getStatusTimes().get(LoanStatusData.ACTIVE)));
            endedOnLabel.setText(String.format("%s %d", timeUnitName, loanViewComponentController.getSelectedLoanDetails().getStatusTimes().get(LoanStatusData.FINISHED)));
        }
    }

}
