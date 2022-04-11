package com.alternabank.engine.account;

import com.alternabank.engine.account.dto.AccountDetails;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

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
    public AccountDetails toAccountDetails() {
        return new AccountDetails(this);
    }

    @Override
    public Transaction.Record.Unilateral executeTransaction(UnilateralTransaction.Type type, double total) {
        Transaction.Unilateral transaction = new UnilateralTransaction(type, total);
        Transaction.Record.Unilateral record = transaction.execute(this);
        ledger.log(record);
        getUnilateralTransactionListenerList().forEach(listener -> listener.unilateralTransactionExecuted(new UnilateralTransactionEvent(this, record)));
        return record;
    }

    @Override
    public Transaction.Record.Bilateral executeTransaction(BilateralTransaction.Type type, Account recipient, double principal, double interest) {
        Transaction.Bilateral transaction = new BilateralTransaction(type, principal, interest);
        Transaction.Record.Bilateral record = recipient.respondToTransaction(this, transaction);
        ledger.log(record);
        return record;
    }

    @Override
    public Transaction.Record.Bilateral respondToTransaction(Transaction.Initiator initiator, Transaction.Bilateral transaction) {
        Transaction.Record.Bilateral record = transaction.execute(initiator, this);
        ledger.log(record);
        getBilateralTransactionListenerList().forEach(listener -> listener.bilateralTransactionExecuted(new BilateralTransactionEvent(initiator, record)));
        return record;
    }

    protected abstract List<UnilateralTransactionListener> getUnilateralTransactionListenerList();

    protected abstract List<BilateralTransactionListener> getBilateralTransactionListenerList();

    @Override
    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        getUnilateralTransactionListenerList().add(listener);
    }

    @Override
    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        getBilateralTransactionListenerList().add(listener);
    }

    @Override
    public List<UnilateralTransactionListener> getUnilateralTransactionListeners() {
        return Collections.unmodifiableList(getUnilateralTransactionListenerList());
    }

    @Override
    public List<BilateralTransactionListener> getBilateralTransactionListeners() {
        return Collections.unmodifiableList(getBilateralTransactionListenerList());
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

        private final Set<Transaction.Record> records = new LinkedHashSet<>();

        @Override
        public Account getAccount() {
            return AbstractAccount.this;
        }

        @Override
        public Set<Transaction.Record> getRecords() {
            return Collections.unmodifiableSet(records);
        }

        private void log(Transaction.Record record) {
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
