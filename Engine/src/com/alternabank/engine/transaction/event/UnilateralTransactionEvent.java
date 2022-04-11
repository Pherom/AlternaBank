package com.alternabank.engine.transaction.event;

import com.alternabank.engine.transaction.Transaction;

public class UnilateralTransactionEvent {

    private final Transaction.Initiator source;
    private final Transaction.Record.Unilateral record;

    public UnilateralTransactionEvent(Transaction.Initiator source, Transaction.Record.Unilateral record) {
        this.source = source;
        this.record = record;
    }

    public Transaction.Initiator getSource() {
        return source;
    }

    public Transaction.Record.Unilateral getRecord() {
        return record;
    }
}
