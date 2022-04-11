package com.alternabank.engine.account;

import com.alternabank.engine.transaction.Transaction;

public interface OwnedAccount extends Account {

    Owner getOwner();

    interface Owner {

        String getName();

        OwnedAccount getAccount();

    }

}
