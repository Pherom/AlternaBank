package com.alternabank.dto.loan.notification;

import com.alternabank.dto.loan.status.LoanStatusData;

public class LoanStatusChangeNotification {

    private final String loanID;

    private final LoanStatusData oldStatus;

    private final LoanStatusData newStatus;

    public LoanStatusChangeNotification(String loanID, LoanStatusData oldStatus, LoanStatusData newStatus) {
        this.loanID = loanID;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public String getLoanID() {
        return loanID;
    }

    public LoanStatusData getOldStatus() {
        return oldStatus;
    }

    public LoanStatusData getNewStatus() {
        return newStatus;
    }

    @Override
    public String toString() {
        return String.format("LOAN STATUS CHANGED:%s\tLoan ID: %s%s\tOld status: %s%s\tNew status: %s", System.lineSeparator(), loanID,
                System.lineSeparator(), oldStatus,
                System.lineSeparator(), newStatus);
    }

}
