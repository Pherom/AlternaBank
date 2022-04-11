package com.alternabank.engine.customer;

import com.alternabank.engine.loan.request.LoanRequest;

import java.util.Set;

public interface Borrower {

    boolean postLoanRequest(LoanRequest loanRequest);

    Set<String> getPostedLoansIDs();

}
