package com.alternabank.engine.loan;

import java.util.Set;

public interface Investment {

    int DEFAULT_VALUE = -1;
    int MINIMUM_TOTAL = 1;
    int INTEREST_PER_TIME_UNIT_LOWER_BOUND = 0;
    int INTEREST_RATE_LOWER_BOUND = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_LOWER_BOUND = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_UPPER_BOUND = 101;
    int MINIMUM_MAXIMUM_BORROWER_ACTIVE_LOANS = 0;

    Request getOriginalRequest();

    interface Request {

        String getLenderName();

        double getTotal();

        Set<String> getCategoriesOfInterest();

        double getMinimumInterestRate();

        double getMinimumInterestPerTimeUnit();

        int getMinimumLoanTerm();

        int getMaximumLoanOwnershipPercentage();

        int getMaximumBorrowerActiveLoans();

        Set<String> getChosenLoanIDs();

        int getChosenLoanCount();
    }

}
