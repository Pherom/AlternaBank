package com.alternabank.dto.transaction;

import com.alternabank.dto.transaction.status.TransactionStatusData;

import java.util.Objects;

public abstract class TransactionRecord {

    protected final String timeUnitName;
    private final int transactionID;
    private final int executionTime;
    private final String initiatorID;

    private final double total;
    private final double initiatorBalanceBefore;
    private final double initiatorBalanceAfter;
    private final TransactionStatusData status;

    protected TransactionRecord(String timeUnitName, int transactionID, int executionTime, String initiatorID, double total, double initiatorBalanceBefore, double initiatorBalanceAfter, TransactionStatusData status) {
        this.timeUnitName = timeUnitName;
        this.transactionID = transactionID;
        this.executionTime = executionTime;
        this.initiatorID = initiatorID;
        this.total = total;
        this.initiatorBalanceBefore = initiatorBalanceBefore;
        this.initiatorBalanceAfter = initiatorBalanceAfter;
        this.status = status;
    }

    public int getID() {
        return transactionID;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public String getInitiatorID() {
        return initiatorID;
    }

    public double getTotal() {
        return total;
    }

    public TransactionStatusData getStatus() {
        return status;
    }

    public double getInitiatorBalanceBefore() {
        return initiatorBalanceBefore;
    }

    public double getInitiatorBalanceAfter() {
        return initiatorBalanceAfter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRecord record = (TransactionRecord) o;
        return transactionID == record.getID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionID);
    }
}
