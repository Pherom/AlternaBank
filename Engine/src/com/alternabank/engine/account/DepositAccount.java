package com.alternabank.engine.account;

import com.alternabank.engine.transaction.Transaction;

public interface DepositAccount extends Account {

    Deposit getDeposit();

    interface Deposit {

        String getID();

        DepositAccount getAccount();

    }

}
