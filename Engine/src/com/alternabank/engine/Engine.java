package com.alternabank.engine;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.xml.event.listener.*;

import java.nio.file.Path;
import java.util.Set;

public interface Engine {

    void loadFromXMLFile(Path filePath);

    Set<String> getCustomerNames();

    Set<LoanDetails> getLoanDetails();

    Set<CustomerDetails> getCustomerDetails();

    Set<String> getLoanCategories();

    Set<CustomerBalanceDetails> getCustomerBalanceDetails();

    void customerDepositFunds(String customerName, double total);

    void customerWithdrawFunds(String customerName, double total);

    void addXMLFileLoadFailureListener(XMLFileLoadFailureListener listener);

    void addXMLCategoryLoadFailureListener(XMLCategoryLoadFailureListener listener);

    void addXMLCustomerLoadFailureListener(XMLCustomerLoadFailureListener listener);

    void addXMLLoanLoadFailureListener(XMLLoanLoadFailureListener listener);

    void addXMLLoadSuccessListener(XMLLoadSuccessListener listener);

    void addUnilateralTransactionListener(UnilateralTransactionListener listener);

    void addBilateralTransactionListener(BilateralTransactionListener listener);

    boolean postInvestmentRequest(Investment.Request investmentRequest);

    void advanceTime();

    void exit();

    int getCurrentTime();

    String getTimeUnit();

}
