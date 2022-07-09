package com.alternabank.dto.transaction;

import com.alternabank.dto.transaction.status.TransactionStatusData;
import com.alternabank.dto.transaction.type.UnilateralTransactionTypeData;

public class UnilateralTransactionRecord extends TransactionRecord {

    private final UnilateralTransactionTypeData type;

    public UnilateralTransactionRecord(String timeUnitName, int transactionID, UnilateralTransactionTypeData type, int executionTime, String initiatorID, double total, double initiatorBalanceBefore, double initiatorBalanceAfter, TransactionStatusData status) {
        super(timeUnitName, transactionID, executionTime, initiatorID, total, initiatorBalanceBefore, initiatorBalanceAfter, status);
        this.type = type;
    }

    public UnilateralTransactionTypeData getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format(
                "(%s %d) %s TRANSACTION:" + System.lineSeparator()
                        + "\tInitiator: %s (Balance: %.2f -> %.2f)" + System.lineSeparator()
                        + "\tTotal: %.2f" + System.lineSeparator()
                        + "\tStatus: %s",
                timeUnitName,
                getExecutionTime(),
                type, getInitiatorID(), getInitiatorBalanceBefore(),
                getInitiatorBalanceAfter(), getTotal(), getStatus());
    }

}
