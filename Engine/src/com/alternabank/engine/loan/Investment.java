package com.alternabank.engine.loan;

import java.util.Set;

public interface Investment {

    int MINIMUM_INTEREST = 0;
    int MINIMUM_TOTAL = 1;
    int MINIMUM_INTEREST_RATE = 0;
    int MINIMUM_LOAN_TERM_MIN = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MIN = 0;
    int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX = 100;
    int MAXIMUM_BORROWER_ACTIVE_LOANS_MIN = 0;

    Request getOriginalRequest();

    interface Request {

        String getLenderName();

        double getTotal();

        Set<String> getCategoriesOfInterest();

        double getMinimumInterestRate();

        double getMinimumInterest();

        int getMinimumLoanTerm();

        int getMaximumLoanOwnershipPercentage();

        int getMaximumBorrowerActiveLoans();

        Set<String> getChosenLoanIDs();

        int getChosenLoanCount();
    }

}
