package com.alternabank.engine.loan.event;

import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.engine.loan.Loan;
import com.alternabank.dto.loan.LoanDetails;

public class LoanStatusUpdateEvent {

    private final LoanStatusData oldStatus;
    private final LoanDetails loanDetails;

    public LoanStatusUpdateEvent(LoanStatusData oldStatus, LoanDetails loanDetails) {
        this.oldStatus = oldStatus;
        this.loanDetails = loanDetails;
    }

    public LoanStatusData getOldStatus() {
        return oldStatus;
    }

    public LoanStatusData getNewStatus() {
        return loanDetails.getStatus();
    }

    public LoanDetails getLoanDetails() {
        return loanDetails;
    }
}
