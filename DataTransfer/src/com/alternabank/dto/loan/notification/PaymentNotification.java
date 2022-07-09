package com.alternabank.dto.loan.notification;

public class PaymentNotification {

    private String timeUnitName;
    private final String loanID;

    private final int paymentTime;

    private final double paymentInterest;

    private final double paymentPrincipal;

    public PaymentNotification(String timeUnitName, String loanID, int paymentTime, double paymentPrincipal, double paymentInterest) {
        this.timeUnitName = timeUnitName;
        this.loanID = loanID;
        this.paymentTime = paymentTime;
        this.paymentPrincipal = paymentPrincipal;
        this.paymentInterest = paymentInterest;
    }

    public String getLoanID() {
        return loanID;
    }

    public int getPaymentTime() {
        return paymentTime;
    }

    public double getPaymentPrincipal() {
        return paymentPrincipal;
    }

    public double getPaymentInterest() {
        return paymentInterest;
    }

    public double getPaymentTotal() {
        return paymentInterest + paymentPrincipal;
    }

    @Override
    public String toString() {
        return String.format("PAYMENT NOTIFICATION:%s\tLoan ID: %s%s\tDue on: %s %d%s\tTotal: %.2f (Principal: %.2f | Interest: %.2f)", System.lineSeparator(), loanID,
                System.lineSeparator(), timeUnitName, paymentTime,
                System.lineSeparator(), getPaymentTotal(), paymentPrincipal, paymentInterest);
    }

}
