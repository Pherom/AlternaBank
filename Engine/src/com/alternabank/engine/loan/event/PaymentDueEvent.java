package com.alternabank.engine.loan.event;

import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.dto.loan.notification.PaymentNotification;

public class PaymentDueEvent {

    private PaymentNotification paymentNotification;
    private LoanDetails loanDetails;

    public PaymentDueEvent(PaymentNotification paymentNotification, LoanDetails loanDetails) {
        this.paymentNotification = paymentNotification;
        this.loanDetails = loanDetails;
    }

    public PaymentNotification getNotification() {
        return paymentNotification;
    }

    public LoanDetails getLoanDetails() {
        return loanDetails;
    }

}
