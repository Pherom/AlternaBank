package com.alternabank.engine;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.xml.XMLFileLoader;
import com.alternabank.engine.xml.XMLLoader;
import com.alternabank.engine.xml.event.listener.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AlternaBankEngine implements Engine{

    private static AlternaBankEngine instance = null;
    private final XMLLoader xmlFileLoader = new XMLFileLoader();

    public static Engine getInstance() {
        if(instance == null)
            instance = new AlternaBankEngine();
        return instance;
    }

    private AlternaBankEngine() {

    }

    @Override
    public void loadFromXMLFile(Path filePath) {
        xmlFileLoader.loadSystemFromFile(filePath);
    }

    @Override
    public Set<String> getCustomerNames() {
        return Collections.unmodifiableSet(CustomerManager.getInstance().getCustomersByName().keySet());
    }


    @Override
    public Set<LoanDetails> getLoanDetails() {
        return LoanManager.getInstance().getLoansByID().values().stream().map(Loan::toLoanDetails).collect(Collectors.toSet());
    }

    @Override
    public Set<CustomerDetails> getCustomerDetails() {
        return CustomerManager.getInstance().getCustomersByName().values().stream().map(CustomerManager.Customer::toCustomerDetails).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getLoanCategories() {
        return LoanManager.getInstance().getAvailableCategories();
    }

    @Override
    public Set<CustomerBalanceDetails> getCustomerBalanceDetails() {
        return CustomerManager.getInstance().getCustomerBalanceDetails();
    }

    @Override
    public void customerDepositFunds(String customerName, double total) {
        if(CustomerManager.getInstance().customerExists(customerName)) {
            CustomerManager.Customer customer = CustomerManager.getInstance().getCustomersByName().get(customerName);
            customer.getAccount().executeTransaction(UnilateralTransaction.Type.DEPOSIT, total);
        }
    }

    @Override
    public void customerWithdrawFunds(String customerName, double total) {
        if(CustomerManager.getInstance().customerExists(customerName)) {
            CustomerManager.Customer customer = CustomerManager.getInstance().getCustomersByName().get(customerName);
            customer.getAccount().executeTransaction(UnilateralTransaction.Type.WITHDRAWAL, total);
        }
    }

    @Override
    public void addXMLFileLoadFailureListener(XMLFileLoadFailureListener listener) {
        xmlFileLoader.addFileLoadFailureListener(listener);
    }

    @Override
    public void addXMLCategoryLoadFailureListener(XMLCategoryLoadFailureListener listener) {
        xmlFileLoader.addCategoryLoadFailureListener(listener);
    }

    @Override
    public void addXMLCustomerLoadFailureListener(XMLCustomerLoadFailureListener listener) {
        xmlFileLoader.addCustomerLoadFailureListener(listener);
    }

    @Override
    public void addXMLLoanLoadFailureListener(XMLLoanLoadFailureListener listener) {
        xmlFileLoader.addLoanLoadFailureListener(listener);
    }

    @Override
    public void addXMLLoadSuccessListener(XMLLoadSuccessListener listener) {
        xmlFileLoader.addLoadSuccessListener(listener);
    }

    @Override
    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        CustomerManager.getInstance().addUnilateralTransactionListener(listener);
        LoanManager.getInstance().addUnilateralTransactionListener(listener);
    }

    @Override
    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        CustomerManager.getInstance().addBilateralTransactionListener(listener);
        LoanManager.getInstance().addBilateralTransactionListener(listener);
    }

    @Override
    public boolean postInvestmentRequest(Investment.Request investmentRequest) {
        return CustomerManager.getInstance().getCustomersByName().get((investmentRequest.getLenderName())).postInvestmentRequest(investmentRequest);
    }

    @Override
    public void advanceTime() {
        TimeManager.getInstance().advanceTime();
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public int getCurrentTime() {
        return TimeManager.getInstance().getCurrentTime();
    }

    @Override
    public String getTimeUnit() {
        return TimeManager.getInstance().getTimeUnitName();
    }

}
