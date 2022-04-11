package com.alternabank.engine.account.dto;

import com.alternabank.engine.account.Account;
import com.alternabank.engine.transaction.Transaction;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class AccountDetails {

    private final String accountAsString;
    private final String id;
    private final double balance;
    private final Set<Transaction.Record> transactionRecords;

    public AccountDetails(Account account) {
        accountAsString = account.toString();
        id = account.getID();
        balance = account.getBalance();
        transactionRecords = new LinkedHashSet<>(account.getLedger().getRecords());
    }

    public String getAccountAsString() {
        return accountAsString;
    }

    public String getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public Set<Transaction.Record> getTransactionRecords() {
        return Collections.unmodifiableSet(transactionRecords);
    }
}
