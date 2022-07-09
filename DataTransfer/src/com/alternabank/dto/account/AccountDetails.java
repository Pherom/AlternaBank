package com.alternabank.dto.account;

import com.alternabank.dto.transaction.TransactionRecord;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AccountDetails {

    private final String accountAsString;
    private final String id;
    private final double balance;
    private final List<TransactionRecord> transactionRecords;

    private final int ledgerVersion;

    public AccountDetails(String accountAsString, String id, double balance, List<TransactionRecord> transactionRecords, int ledgerVersion) {
        this.accountAsString = accountAsString;
        this.id = id;
        this.balance = balance;
        this.transactionRecords = new ArrayList<>(transactionRecords);
        this.ledgerVersion = ledgerVersion;
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

    public List<TransactionRecord> getTransactionRecords() {
        return transactionRecords;
    }

    public int getLedgerVersion() {
        return ledgerVersion;
    }
}
