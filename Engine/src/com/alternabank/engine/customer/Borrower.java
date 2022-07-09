package com.alternabank.engine.customer;

import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.engine.loan.Loan;

import java.util.Set;

public interface Borrower {

    String getName();

    boolean postedLoan(String loanID);

    boolean postLoanRequest(LoanRequest loanRequest);

    Loan getPostedLoan(String loanID);

    int getActiveLoanCount();

    Set<String> getPostedLoanIDs();

}
