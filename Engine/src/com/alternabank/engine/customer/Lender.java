package com.alternabank.engine.customer;

import com.alternabank.dto.loan.request.InvestmentRequest;

import java.util.Set;

public interface Lender {

    String getName();

    boolean postInvestmentRequest(InvestmentRequest investmentRequest);

    boolean postRemainingLoanPortionForSale(String loanID);

    boolean buyRemainingLoanPortion(String loanID, String lenderName);

    Set<String> getInvestedLoanIDs();

}
