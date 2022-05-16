package com.alternabank.engine.loan.event;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;

public class LoanStatusUpdateEvent {

    private final Loan.Status oldStatus;
    private final LoanDetails loanDetails;

    public LoanStatusUpdateEvent(Loan.Status oldStatus, LoanDetails loanDetails) {
        this.oldStatus = oldStatus;
        this.loanDetails = loanDetails;
    }

    public Loan.Status getOldStatus() {
        return oldStatus;
    }

    public Loan.Status getNewStatus() {
        return loanDetails.getStatus();
    }

    public LoanDetails getLoanDetails() {
        return loanDetails;
    }
}
