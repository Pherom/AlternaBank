package com.alternabank.engine.loan.request;

import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.user.UserManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.alternabank.engine.loan.Investment.DEFAULT_VALUE;

public class InvestmentRequestBuilder {

    private String lenderName;
    private double total;
    private Set<String> categoriesOfInterest = UserManager.getInstance().getAdmin().getLoanManager().getAvailableCategories();
    private double minimumInterestPerTimeUnit = DEFAULT_VALUE;
    private double minimumInterestRate = DEFAULT_VALUE;
    private int minimumLoanTerm = DEFAULT_VALUE;
    private int maximumLoanOwnershipPercentage = DEFAULT_VALUE;
    private int maximumBorrowerActiveLoans = DEFAULT_VALUE;
    private final Set<String> chosenLoanIDs = new HashSet<>();

    public InvestmentRequestBuilder(String lenderName, double total) {
        this.lenderName = lenderName;
        this.total = total;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setCategoriesOfInterest(Set<String> categoriesOfInterest) {
        this.categoriesOfInterest = categoriesOfInterest;
    }

    public void setMinimumInterestPerTimeUnit(double minimumInterestPerTimeUnit) {
        this.minimumInterestPerTimeUnit = DEFAULT_VALUE;
        this.minimumInterestPerTimeUnit = minimumInterestPerTimeUnit;
    }

    public void setMinimumInterestRate(double minimumInterestRate) {
        this.minimumInterestPerTimeUnit = DEFAULT_VALUE;
        this.minimumInterestRate = minimumInterestRate;
    }

    public void setMinimumLoanTerm(int minimumLoanTerm) {
        this.minimumLoanTerm = minimumLoanTerm;
    }

    public void setMaximumLoanOwnershipPercentage(int maximumLoanOwnershipPercentage) {
        this.maximumLoanOwnershipPercentage = maximumLoanOwnershipPercentage;
    }

    public void setMaximumBorrowerActiveLoans(int maximumBorrowerActiveLoans) {
        this.maximumBorrowerActiveLoans = maximumBorrowerActiveLoans;
    }

    public void addLoanToInvestIn(String loanID) {
        chosenLoanIDs.add(loanID);
    }

    public void addLoansToInvestIn(Collection<String> loanIDs) {
        chosenLoanIDs.addAll(loanIDs);
    }

    public String getLenderName() {
        return lenderName;
    }

    public double getTotal() {
        return total;
    }

    public Set<String> getCategoriesOfInterest() {
        return Collections.unmodifiableSet(categoriesOfInterest);
    }

    public double getMinimumInterestRate() {
        return minimumInterestRate;
    }

    public double getMinimumInterestPerTimeUnit() {
        return minimumInterestPerTimeUnit;
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

    public Set<String> getChosenLoanIDs() {
        return Collections.unmodifiableSet(chosenLoanIDs);
    }

    public InvestmentRequest build() {
            return new InvestmentRequest(lenderName, total, categoriesOfInterest, minimumInterestPerTimeUnit, minimumInterestRate, minimumLoanTerm, maximumLoanOwnershipPercentage, maximumBorrowerActiveLoans, chosenLoanIDs);

    }
}
