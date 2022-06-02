package com.alternabank.engine.loan.request;

import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.user.UserManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.alternabank.engine.loan.Investment.*;

public class InvestmentRequestBuilder {

    private String lenderName;
    private double total;
    private Set<String> categoriesOfInterest = UserManager.getInstance().getAdmin().getLoanManager().getAvailableCategories();
    private double minimumInterest = MINIMUM_INTEREST;
    private double minimumInterestRate = MINIMUM_INTEREST_RATE;
    private int minimumLoanTerm = MINIMUM_LOAN_TERM_MIN;
    private int maximumLoanOwnershipPercentage = MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX;
    private int maximumBorrowerActiveLoans = UserManager.getInstance().getAdmin().getLoanManager().getPostedLoanCountOfCustomerWithMostPostedLoans();
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

    public void setMinimumInterest(double minimumInterest) {
        this.minimumInterest = minimumInterest;
    }

    public void setMinimumInterestRate(double minimumInterestRate) {
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

    public Set<String> getChosenLoanIDs() {
        return chosenLoanIDs;
    }

    public InvestmentRequest build() {
            return new InvestmentRequest(lenderName, total, categoriesOfInterest, minimumInterest, minimumInterestRate, minimumLoanTerm, maximumLoanOwnershipPercentage, maximumBorrowerActiveLoans, chosenLoanIDs);

    }
}
