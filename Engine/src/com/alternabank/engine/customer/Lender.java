package com.alternabank.engine.customer;

import com.alternabank.engine.loan.Investment;

import java.util.Set;

public interface Lender {

    boolean postInvestmentRequest(Investment.Request investmentRequest);

    Set<String> getInvestedLoansIDs();

}
