package com.alternabank.engine.transaction.event;

import com.alternabank.dto.transaction.UnilateralTransactionRecord;
import com.alternabank.engine.transaction.Transaction;

public class UnilateralTransactionEvent {

    private final Transaction.Initiator source;
    private final UnilateralTransactionRecord record;

    public UnilateralTransactionEvent(Transaction.Initiator source, UnilateralTransactionRecord record) {
        this.source = source;
        this.record = record;
    }

    public Transaction.Initiator getSource() {
        return source;
    }

    public UnilateralTransactionRecord getRecord() {
        return record;
    }
}
