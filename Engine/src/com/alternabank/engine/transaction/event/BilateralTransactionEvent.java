package com.alternabank.engine.transaction.event;

import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.engine.transaction.Transaction;

import java.util.Objects;

public class BilateralTransactionEvent {

    private final Transaction.Initiator source;
    private final BilateralTransactionRecord record;

    public BilateralTransactionEvent(Transaction.Initiator source, BilateralTransactionRecord record) {
        this.source = source;
        this.record = record;
    }

    public Transaction.Initiator getSource() {
        return source;
    }

    public BilateralTransactionRecord getRecord() {
        return record;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BilateralTransactionEvent that = (BilateralTransactionEvent) o;
        return Objects.equals(source, that.source) && Objects.equals(record, that.record);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, record);
    }
}
