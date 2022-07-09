package com.alternabank.dto.loan.request;

import java.util.Objects;

public class LoanRequest {
    private final String category;
    private final double capital;
    private final int installmentPeriod;
    private final double interestPerInstallment;
    private final int term;
    private final String id;
    private final String borrowerName;

    public static LoanRequest createByInterestRate(String borrowerName, String category, double capital, int installmentPeriod, double interestRate, int term, String id) {
        return new LoanRequest(borrowerName, category, capital, installmentPeriod, (capital * interestRate) / (double)(term / installmentPeriod), term, id);
    }

    public static LoanRequest createByInterestPerPayment(String borrowerName, String category, double capital, int installmentPeriod, double interestPerInstallment, int term, String id) {
        return new LoanRequest(borrowerName, category, capital, installmentPeriod, interestPerInstallment, term, id);
    }

    private LoanRequest(String borrowerName, String category, double capital, int installmentPeriod, double interestPerInstallment, int term, String id) {
        this.borrowerName = borrowerName;
        this.category = category;
        this.capital = capital;
        this.installmentPeriod = installmentPeriod;
        this.interestPerInstallment = interestPerInstallment;
        this.term = term;
        this.id = id;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public String getCategory() {
        return category;
    }

    public double getCapital() {
        return capital;
    }

    public double getTotalInterest() {
        return interestPerInstallment * getInstallmentCount();
    }

    public double getTotal() {
        return capital + getTotalInterest();
    }

    public int getInstallmentPeriod() {
        return installmentPeriod;
    }

    public double getInterestPerInstallment() {
        return interestPerInstallment;
    }

    public double getInterestPerTimeUnit() {
        return interestPerInstallment / installmentPeriod;
    }

    public double getPrincipalPerInstallment() {
        return capital / getInstallmentCount();
    }

    public double getTotalPerInstallment() {
        return getInterestPerInstallment() + getPrincipalPerInstallment();
    }

    public int getInstallmentCount() {
        return term / installmentPeriod;
    }

    public double getInterestRate() {
        return interestPerInstallment / getPrincipalPerInstallment();
    }

    public int getTerm() {
        return term;
    }

    public String getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanRequest that = (LoanRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
