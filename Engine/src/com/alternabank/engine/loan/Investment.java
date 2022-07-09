package com.alternabank.engine.loan;

import com.alternabank.dto.loan.InvestmentDetails;
import com.alternabank.dto.loan.request.InvestmentRequest;

public interface Investment {

    int MINIMUM_INTEREST = 0;
    int MINIMUM_TOTAL = 1;
    int MINIMUM_INTEREST_RATE = 0;
    int MINIMUM_LOAN_TERM_MIN = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MIN = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX = 100;
    int MAXIMUM_BORROWER_ACTIVE_LOANS_MIN = 0;

    InvestmentRequest getOriginalRequest();

    String getLoanID();

    double getInvestmentTotal();

    InvestmentDetails toDTO();

    void setForSale(boolean forSale);

    boolean getForSale();
}
