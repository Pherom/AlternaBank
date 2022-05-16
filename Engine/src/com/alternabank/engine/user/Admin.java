package com.alternabank.engine.user;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.transaction.TransactionManager;
import com.alternabank.engine.xml.XMLFileLoader;
import com.alternabank.engine.xml.XMLLoader;

public class Admin implements User {

    private final String name = "Admin";
    private final XMLLoader xmlFileLoader = new XMLFileLoader(this);
    private final TimeManager timeManager = new TimeManager(this);
    private final CustomerManager customerManager = new CustomerManager(this);

    private final LoanManager loanManager = new LoanManager(this);

    private final TransactionManager transactionManager = new TransactionManager(this);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void exitApplication() {
        System.exit(0);
    }

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

    public void advanceTime() {
        timeManager.advanceTime();
    }
}
