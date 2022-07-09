package com.alternabank.engine.transaction;

import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.Engine;

public class TransactionManager {

    private final Engine engine;

    public TransactionManager(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        engine.getCustomerManager().addUnilateralTransactionListener(listener);
        engine.getLoanManager().addUnilateralTransactionListener(listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        engine.getCustomerManager().addBilateralTransactionListener(listener);
        engine.getLoanManager().addBilateralTransactionListener(listener);
    }

}
