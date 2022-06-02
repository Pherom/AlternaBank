package com.alternabank.engine.loan.request;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.user.UserManager;

import java.util.Objects;

public class LoanRequest implements Loan.Request {

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

    @Override
    public String getBorrowerName() {
        return borrowerName;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public double getCapital() {
        return capital;
    }

    @Override
    public double getTotalInterest() {
        return interestPerInstallment * getInstallmentCount();
    }

    @Override
    public double getTotal() {
        return capital + getTotalInterest();
    }

    @Override
    public int getInstallmentPeriod() {
        return installmentPeriod;
    }

    @Override
    public double getInterestPerInstallment() {
        return interestPerInstallment;
    }

    @Override
    public double getInterestPerTimeUnit() {
        return interestPerInstallment / installmentPeriod;
    }

    @Override
    public double getPrincipalPerInstallment() {
        return capital / getInstallmentCount();
    }

    @Override
    public double getTotalPerInstallment() {
        return getInterestPerInstallment() + getPrincipalPerInstallment();
    }

    @Override
    public int getInstallmentCount() {
        return term / installmentPeriod;
    }

    @Override
    public double getInterestRate() {
        return interestPerInstallment / getPrincipalPerInstallment();
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String toString() {
        String timeUnit = UserManager.getInstance().getAdmin().getTimeManager().getTimeUnitName();
        return String.format(
                "LOAN DETAILS:" + System.lineSeparator()
                        + "\tID: %s" + System.lineSeparator()
                        + "\tBorrower: %s" + System.lineSeparator()
                        + "\tCategory: %s" + System.lineSeparator()
                        + "\tCapital: %.2f (%.2f every %d %s for %d %s)" + System.lineSeparator()
                        + "\tInterest: %.2f%% (Total: %.2f | %.2f every %d %s)" + System.lineSeparator()
                        + "\tTotal: %.2f",
                id, borrowerName, category, capital, getPrincipalPerInstallment(), installmentPeriod, timeUnit,
                term, timeUnit, getInterestRate() * 100, getTotalInterest(), interestPerInstallment, installmentPeriod, timeUnit, getTotal());
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
