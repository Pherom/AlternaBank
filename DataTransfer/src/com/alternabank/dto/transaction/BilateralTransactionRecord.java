package com.alternabank.dto.transaction;

import com.alternabank.dto.transaction.status.TransactionStatusData;
import com.alternabank.dto.transaction.type.BilateralTransactionTypeData;

public class BilateralTransactionRecord extends TransactionRecord {
    private final BilateralTransactionTypeData type;
    private final String recipientID;

    private double principalPart;

    private double interestPart;

    private final double recipientBalanceBefore;
    private final double recipientBalanceAfter;

    public BilateralTransactionRecord(String timeUnitName, int transactionID, BilateralTransactionTypeData type, int executionTime, String initiatorID, double principalPart, double interestPart, double initiatorBalanceBefore, double initiatorBalanceAfter, String recipientID, double recipientBalanceBefore, double recipientBalanceAfter, TransactionStatusData status) {
        super(timeUnitName, transactionID, executionTime, initiatorID, principalPart + interestPart, initiatorBalanceBefore, initiatorBalanceAfter, status);
        this.type = type;
        this.recipientID = recipientID;
        this.principalPart = principalPart;
        this.interestPart = interestPart;
        this.recipientBalanceBefore = recipientBalanceBefore;
        this.recipientBalanceAfter = recipientBalanceAfter;
    }

    public BilateralTransactionTypeData getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format(
                "(%s %d) %s TRANSACTION:" + System.lineSeparator()
                        + "\tInitiator: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                        + "\tRecipient: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                        + "\tTotal: %.2f (Principal: %.2f, Interest: %.2f)" + System.lineSeparator()
                        + "\tStatus: %s",
                timeUnitName,
                getExecutionTime(),
                type, getInitiatorID(), getInitiatorBalanceBefore(), getInitiatorBalanceAfter(),
                getRecipientID(), getRecipientBalanceBefore(), getRecipientBalanceAfter(),
                getTotal(), getPrincipalPart(), getInterestPart(), getStatus());
    }

    public String getRecipientID() {
        return recipientID;
    }

    public double getPrincipalPart() {
        return principalPart;
    }

    public double getInterestPart() {
        return interestPart;
    }

    public double getRecipientBalanceBefore() {
        return recipientBalanceBefore;
    }

    public double getRecipientBalanceAfter() {
        return recipientBalanceAfter;
    }
}
