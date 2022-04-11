package com.alternabank.engine.loan.request;

import com.alternabank.engine.loan.Investment;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class InvestmentRequest implements Investment.Request{

    private final String lenderName;
    private final double total;
    private final Set<String> categoriesOfInterest;
    private final double minimumInterestRate;
    private final double minimumInterestPerTimeUnit;
    private final int minimumLoanTerm;
    private final int maximumLoanOwnershipPercentage;
    private final int maximumBorrowerActiveLoans;
    private final Set<String> chosenLoanIDs;

    public InvestmentRequest(String lenderName, double total, Set<String> categoriesOfInterest, double minimumInterestPerTimeUnit, double minimumInterestRate, int minimumLoanTerm, int maximumLoanOwnershipPercentage, int maximumBorrowerActiveLoans, Set<String> chosenLoanIDs) {
        this.lenderName = lenderName;
        this.total = total;
        this.categoriesOfInterest = Collections.unmodifiableSet(categoriesOfInterest);
        this.minimumInterestPerTimeUnit = minimumInterestPerTimeUnit;
        this.minimumInterestRate = minimumInterestRate;
        this.minimumLoanTerm = minimumLoanTerm;
        this.maximumLoanOwnershipPercentage = maximumLoanOwnershipPercentage;
        this.maximumBorrowerActiveLoans = maximumBorrowerActiveLoans;
        this.chosenLoanIDs = chosenLoanIDs;
    }

    @Override
    public String getLenderName() {
        return lenderName;
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public Set<String> getCategoriesOfInterest() {
        return categoriesOfInterest;
    }

    @Override
    public double getMinimumInterestRate() {
        return minimumInterestRate;
    }

    @Override
    public double getMinimumInterestPerTimeUnit() {
        return minimumInterestPerTimeUnit;
    }

    @Override
    public int getMinimumLoanTerm() {
        return minimumLoanTerm;
    }

    @Override
    public int getMaximumLoanOwnershipPercentage() {
        return maximumLoanOwnershipPercentage;
    }

    @Override
    public int getMaximumBorrowerActiveLoans() {
        return maximumBorrowerActiveLoans;
    }

    @Override
    public Set<String> getChosenLoanIDs() {
        return Collections.unmodifiableSet(chosenLoanIDs);
    }

    @Override
    public int getChosenLoanCount() {
        return chosenLoanIDs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvestmentRequest that = (InvestmentRequest) o;
        return Double.compare(that.total, total) == 0 && Double.compare(that.minimumInterestPerTimeUnit, minimumInterestPerTimeUnit) == 0 && minimumLoanTerm == that.minimumLoanTerm && maximumLoanOwnershipPercentage == that.maximumLoanOwnershipPercentage && maximumBorrowerActiveLoans == that.maximumBorrowerActiveLoans && Objects.equals(lenderName, that.lenderName) && Objects.equals(categoriesOfInterest, that.categoriesOfInterest) && Objects.equals(chosenLoanIDs, that.chosenLoanIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lenderName, total, categoriesOfInterest, minimumInterestPerTimeUnit, minimumLoanTerm, maximumLoanOwnershipPercentage, maximumBorrowerActiveLoans, chosenLoanIDs);
    }
}
