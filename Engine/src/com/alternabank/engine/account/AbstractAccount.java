package com.alternabank.engine.account;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.transaction.TransactionRecord;
import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.UnilateralTransactionRecord;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.sql.Array;
import java.util.*;

public abstract class AbstractAccount implements Account, Transaction.Initiator, Transaction.Recipient{

    private final String id;
    private double balance = 0;
    private final Ledger ledger = new Ledger();

    protected AbstractAccount(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean addFunds(double total) {
        boolean success = true;

        if (total <= 0)
            success = false;

        else balance += total;

        return success;
    }

    @Override
    public boolean deductFunds(double total) {
        boolean success = true;

        if (total <= 0 || total > balance)
            success = false;

        else balance -= total;

        return success;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public Ledger getLedger() {
        return ledger;
    }

    @Override
    public AccountDetails toDTO(int oldLedgerVersion) {
        return new AccountDetails(toString(), id, balance, ledger.getRecords(oldLedgerVersion), ledger.getVersion());
    }

    @Override
    public AccountDetails toDTO() {
        return toDTO(0);
    }

    @Override
    public UnilateralTransactionRecord executeTransaction(UnilateralTransaction.Type type, double total, int executionTime) {
        Transaction.Unilateral transaction = new UnilateralTransaction(type, total);
        UnilateralTransactionRecord record = transaction.execute(this, executionTime);
        ledger.log(record);
        Arrays.stream(getEventListeners().getListeners(UnilateralTransactionListener.class)).forEach(listener -> listener.unilateralTransactionExecuted(new UnilateralTransactionEvent(this, record)));
        return record;
    }

    @Override
    public BilateralTransactionRecord executeTransaction(BilateralTransaction.Type type, Account recipient, double principal, double interest, int executionTime) {
        Transaction.Bilateral transaction = new BilateralTransaction(type, principal, interest);
        BilateralTransactionRecord record = recipient.respondToTransaction(this, transaction, executionTime);
        ledger.log(record);
        return record;
    }

    @Override
    public BilateralTransactionRecord respondToTransaction(Transaction.Initiator initiator, Transaction.Bilateral transaction, int executionTime) {
        BilateralTransactionRecord record = transaction.execute(initiator, this, executionTime);
        ledger.log(record);
        Arrays.stream(getEventListeners().getListeners(BilateralTransactionListener.class)).forEach(listener -> listener.bilateralTransactionExecuted(new BilateralTransactionEvent(initiator, record)));
        return record;
    }

    @Override
    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        getEventListeners().add(UnilateralTransactionListener.class, listener);
    }

    @Override
    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        getEventListeners().add(BilateralTransactionListener.class, listener);
    }

    @Override
    public String toString() {
        return String.format("ACCOUNT DETAILS:" + System.lineSeparator()
                            + "\tID: %s" + System.lineSeparator()
                            + "\tBalance: %.2f" + System.lineSeparator()
                            + "\t%s",
                id, balance, ledger.toString().replace(System.lineSeparator(), System.lineSeparator() + "\t"));
    }

    public class Ledger implements Account.Ledger {

        private final List<TransactionRecord> records = new ArrayList<>();

        public int getVersion() {
            return records.size();
        }

        @Override
        public Account getAccount() {
            return AbstractAccount.this;
        }

        @Override
        public List<TransactionRecord> getRecords() {
            return records;
        }

        @Override
        public List<TransactionRecord> getRecords(int fromIndex) {
            if (fromIndex < 0 || fromIndex > records.size())
                fromIndex = 0;
            return records.subList(fromIndex, records.size());
        }

        private void log(TransactionRecord record) {
            records.add(record);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ledger ledger = (Ledger) o;
            return AbstractAccount.this.equals(ledger.getAccount());
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("ACCOUNT LEDGER:");
            records.forEach(record -> stringBuilder.append(System.lineSeparator()).append("\t").append(record.toString()
                    .replace(System.lineSeparator(), System.lineSeparator() + "\t")));
            return stringBuilder.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAccount that = (AbstractAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
