package com.alternabank.engine.transaction;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.user.Admin;

public class TransactionManager {

    private final Admin admin;

    public TransactionManager(Admin admin) {
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        admin.getCustomerManager().addUnilateralTransactionListener(listener);
        admin.getLoanManager().addUnilateralTransactionListener(listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        admin.getCustomerManager().addBilateralTransactionListener(listener);
        admin.getLoanManager().addBilateralTransactionListener(listener);
    }

}
