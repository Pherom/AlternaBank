package com.alternabank.dto.loan.request;

import java.util.*;

public class InvestmentRequestBuilder {

    private String lenderName;
    private double total;
    private Set<String> categoriesOfInterest;
    private double minimumInterest = InvestmentRequest.MINIMUM_INTEREST;
    private double minimumInterestRate = InvestmentRequest.MINIMUM_INTEREST_RATE;
    private int minimumLoanTerm = InvestmentRequest.MINIMUM_LOAN_TERM_MIN;
    private int maximumLoanOwnershipPercentage = InvestmentRequest.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX;
    private int maximumBorrowerActiveLoans;
    private final List<String> chosenLoanIDs = new ArrayList<>();

    public InvestmentRequestBuilder(Set<String> categoriesOfInterest, String lenderName, double total) {
        this.lenderName = lenderName;
        this.total = total;
        maximumBorrowerActiveLoans = Integer.MAX_VALUE;
        this.categoriesOfInterest = new HashSet<>(categoriesOfInterest);
    }

    public InvestmentRequestBuilder setLenderName(String lenderName) {
        this.lenderName = lenderName;
        return this;
    }

    public InvestmentRequestBuilder setTotal(double total) {
        this.total = total;
        return this;
    }

    public InvestmentRequestBuilder setCategoriesOfInterest(Set<String> categoriesOfInterest) {
        this.categoriesOfInterest = categoriesOfInterest;
        return this;
    }

    public InvestmentRequestBuilder setMinimumInterest(double minimumInterest) {
        this.minimumInterest = minimumInterest;
        return this;
    }

    public InvestmentRequestBuilder setMinimumInterestRate(double minimumInterestRate) {
        this.minimumInterestRate = minimumInterestRate;
        return this;
    }

    public InvestmentRequestBuilder setMinimumLoanTerm(int minimumLoanTerm) {
        this.minimumLoanTerm = minimumLoanTerm;
        return this;
    }

    public InvestmentRequestBuilder setMaximumLoanOwnershipPercentage(int maximumLoanOwnershipPercentage) {
        this.maximumLoanOwnershipPercentage = maximumLoanOwnershipPercentage;
        return this;
    }

    public InvestmentRequestBuilder setMaximumBorrowerActiveLoans(int maximumBorrowerActiveLoans) {
        this.maximumBorrowerActiveLoans = maximumBorrowerActiveLoans;
        return this;
    }

    public InvestmentRequestBuilder addLoanToInvestIn(String loanID) {
        chosenLoanIDs.add(loanID);
        return this;
    }

    public InvestmentRequestBuilder addLoansToInvestIn(Collection<String> loanIDs) {
        chosenLoanIDs.addAll(loanIDs);
        return this;
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

    public InvestmentRequest build() {
            return new InvestmentRequest(lenderName, total, categoriesOfInterest, minimumInterest, minimumInterestRate, minimumLoanTerm, maximumLoanOwnershipPercentage, maximumBorrowerActiveLoans, chosenLoanIDs);

    }
}
