package com.alternabank.dto.loan.request;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class InvestmentRequest {

    public static final int MINIMUM_INTEREST = 0;
    public static final int MINIMUM_TOTAL = 1;
    public static final int MINIMUM_INTEREST_RATE = 0;
    public static final int MINIMUM_LOAN_TERM_MIN = 0;
    public static final int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MIN = 0;
    public static final int MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX = 100;
    public static final int MAXIMUM_BORROWER_ACTIVE_LOANS_MIN = 0;

    private final String lenderName;
    private final double total;
    private final Set<String> categoriesOfInterest;
    private final double minimumInterestRate;
    private final double minimumInterest;
    private final int minimumLoanTerm;
    private final int maximumLoanOwnershipPercentage;
    private final int maximumBorrowerActiveLoans;
    private final List<String> chosenLoanIDs;

    public InvestmentRequest(String lenderName, double total, Set<String> categoriesOfInterest, double minimumInterest, double minimumInterestRate, int minimumLoanTerm, int maximumLoanOwnershipPercentage, int maximumBorrowerActiveLoans, List<String> chosenLoanIDs) {
        this.lenderName = lenderName;
        this.total = total;
        this.categoriesOfInterest = categoriesOfInterest;
        this.minimumInterest = minimumInterest;
        this.minimumInterestRate = minimumInterestRate;
        this.minimumLoanTerm = minimumLoanTerm;
        this.maximumLoanOwnershipPercentage = maximumLoanOwnershipPercentage;
        this.maximumBorrowerActiveLoans = maximumBorrowerActiveLoans;
        this.chosenLoanIDs = chosenLoanIDs;
    }

    public String getLenderName() {
        return lenderName;
    }

    public double getTotal() {
        return total;
    }

    public Set<String> getCategoriesOfInterest() {
        return categoriesOfInterest;
    }

    public double getMinimumInterestRate() {
        return minimumInterestRate;
    }

    public double getMinimumInterest() {
        return minimumInterest;
    }

    public int getMinimumLoanTerm() {
        return minimumLoanTerm;
    }

    public int getMaximumLoanOwnershipPercentage() {
        return maximumLoanOwnershipPercentage;
    }

    public int getMaximumBorrowerActiveLoans() {
        return maximumBorrowerActiveLoans;
    }

    public List<String> getChosenLoanIDs() {
        return chosenLoanIDs;
    }

    public int getChosenLoanCount() {
        return chosenLoanIDs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestmentRequest that = (InvestmentRequest) o;
        return Double.compare(that.total, total) == 0 && Double.compare(that.minimumInterest, minimumInterest) == 0 && minimumLoanTerm == that.minimumLoanTerm && maximumLoanOwnershipPercentage == that.maximumLoanOwnershipPercentage && maximumBorrowerActiveLoans == that.maximumBorrowerActiveLoans && Objects.equals(lenderName, that.lenderName) && Objects.equals(categoriesOfInterest, that.categoriesOfInterest) && Objects.equals(chosenLoanIDs, that.chosenLoanIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lenderName, total, categoriesOfInterest, minimumInterest, minimumLoanTerm, maximumLoanOwnershipPercentage, maximumBorrowerActiveLoans, chosenLoanIDs);
    }
}
