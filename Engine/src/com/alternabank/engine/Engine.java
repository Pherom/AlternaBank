package com.alternabank.engine;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.transaction.TransactionManager;
import com.alternabank.engine.xml.XMLFileLoader;
import com.alternabank.engine.xml.XMLLoader;

public class Engine {

    private final XMLLoader xmlFileLoader = new XMLFileLoader(this);
    private final TimeManager timeManager = new TimeManager(this);
    private final CustomerManager customerManager = new CustomerManager(this);

    private final LoanManager loanManager = new LoanManager(this);

    private final TransactionManager transactionManager = new TransactionManager(this);

    public XMLLoader getXmlFileLoader() {
        return xmlFileLoader;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    public LoanManager getLoanManager() {
        return loanManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
