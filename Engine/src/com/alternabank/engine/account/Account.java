package com.alternabank.engine.account;

import com.alternabank.engine.account.dto.AccountDetails;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.util.List;
import java.util.Set;

public interface Account {

    String getID();

    double getBalance();

    Ledger getLedger();

    AccountDetails toAccountDetails();

    Transaction.Record.Unilateral executeTransaction(UnilateralTransaction.Type type, double total);

    Transaction.Record.Bilateral executeTransaction(BilateralTransaction.Type type, Account Recipient, double principal, double interest);

    Transaction.Record.Bilateral respondToTransaction(Transaction.Initiator initiator, Transaction.Bilateral transaction);

    void addUnilateralTransactionListener(UnilateralTransactionListener listener);

    void addBilateralTransactionListener(BilateralTransactionListener listener);

    List<UnilateralTransactionListener> getUnilateralTransactionListeners();

    List<BilateralTransactionListener> getBilateralTransactionListeners();

    interface Ledger {

        Account getAccount();

        Set<Transaction.Record> getRecords();

    }

}
