package com.alternabank.engine.account;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.transaction.TransactionRecord;
import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.UnilateralTransactionRecord;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import javax.swing.event.EventListenerList;
import java.util.List;
import java.util.Set;

public interface Account {

    String getID();

    double getBalance();

    Ledger getLedger();

    AccountDetails toDTO(int ledgerVersion);

    AccountDetails toDTO();

    UnilateralTransactionRecord executeTransaction(UnilateralTransaction.Type type, double total, int executionTime);

    BilateralTransactionRecord executeTransaction(BilateralTransaction.Type type, Account Recipient, double principal, double interest, int executionTime);

    BilateralTransactionRecord respondToTransaction(Transaction.Initiator initiator, Transaction.Bilateral transaction, int executionTime);

    void addUnilateralTransactionListener(UnilateralTransactionListener listener);

    void addBilateralTransactionListener(BilateralTransactionListener listener);

    EventListenerList getEventListeners();

    interface Ledger {

        Account getAccount();

        int getVersion();

        List<TransactionRecord> getRecords();

        List<TransactionRecord> getRecords(int fromIndex);

    }

}
