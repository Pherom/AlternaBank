package com.alternabank.engine.loan.dto;

import com.alternabank.engine.loan.Loan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoanDetails {

    private final String loanAsShortString;
    private final String loanAsString;
    private final String id;
    private final String borrowerName;
    private final String category;
    private final Loan.Status status;
    private final Map<Loan.Status, Integer> statusTimes;
    private final Map<String, Double> investmentByLenderName;
    private final int originalTerm;
    private final int remainingTerm;
    private final int installmentPeriod;
    private final int delayedInstallmentCount;
    private final int previousInstallmentTime;
    private final int nextInstallmentTime;
    private final double totalInvestment;
    private final double interestPerInstallment;
    private final double capital;
    private final double paidInterest;
    private final double remainingInterest;
    private final double remainingPrincipal;
    private final double paidPrincipal;

    public LoanDetails(Loan loan) {
        loanAsShortString = loan.toShortString();
        loanAsString = loan.toString();
        id = loan.getOriginalRequest().getID();
        borrowerName = loan.getOriginalRequest().getBorrowerName();
        category = loan.getOriginalRequest().getCategory();
        status = loan.getStatus();
        statusTimes = new HashMap<>(loan.getStatusTimes());
        totalInvestment = loan.getTotalInvestment();
        investmentByLenderName = new HashMap<>(loan.getInvestmentByLenderName());
        originalTerm = loan.getOriginalRequest().getTerm();
        remainingTerm = loan.getRemainingTerm();
        installmentPeriod = loan.getOriginalRequest().getInstallmentPeriod();
        delayedInstallmentCount = loan.getDelayedInstallmentCount();
        previousInstallmentTime = loan.getPreviousInstallmentTime();
        nextInstallmentTime = loan.getNextInstallmentTime();
        interestPerInstallment = loan.getOriginalRequest().getInterestPerInstallment();
        capital = loan.getOriginalRequest().getCapital();
        paidInterest = loan.getPaidInterest();
        remainingInterest = loan.getRemainingInterest();
        remainingPrincipal = loan.getRemainingPrincipal();
        paidPrincipal = loan.getPaidPrincipal();
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
        return interestPerInstallment + getPrincipalPerInstallment();
    }

    public int getInstallmentCount() {
        return originalTerm / installmentPeriod;
    }

    public double getInterestRate() {
        return interestPerInstallment / getPrincipalPerInstallment();
    }

    public int getOriginalTerm() {
        return originalTerm;
    }

    public String getId() {
        return id;
    }

    public Loan.Status getStatus() {
        return status;
    }

    public Map<Loan.Status, Integer> getStatusTimes() {
        return Collections.unmodifiableMap(statusTimes);
    }

    public Map<String, Double> getInvestmentByLenderName() {
        return Collections.unmodifiableMap(investmentByLenderName);
    }

    public int getRemainingTerm() {
        return remainingTerm;
    }

    public int getPassedTerm() {
        return originalTerm - remainingTerm;
    }

    public int getPassedInstallmentCount() {
        return getPassedTerm() / installmentPeriod;
    }

    public int getRemainingInstallmentCount() {
        return getInstallmentCount() - getPassedInstallmentCount();
    }

    public double getPaidPrincipal() {
        return remainingPrincipal;
    }

    public double getPaidInterest() {
        return paidInterest;
    }

    public double getPaidTotal() {
        return paidInterest + paidPrincipal;
    }

    public double getRemainingInterest() {
        return remainingInterest;
    }

    public double getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public double getRemainingTotal() {
        return remainingInterest + remainingPrincipal;
    }

    public double getTotalInvestment() {
        return totalInvestment;
    }

    public double getRemainingInvestment() {
        return capital - totalInvestment;
    }

    public int getDelayedInstallmentCount() {
        return delayedInstallmentCount;
    }

    double getDelayedInstallmentPrincipal() {
        return delayedInstallmentCount * getPrincipalPerInstallment();
    }

    double getDelayedInstallmentInterest() {
        return delayedInstallmentCount * getInterestPerInstallment();
    }

    public double getDelayedInstallmentTotal() {
        return delayedInstallmentCount * getTotalPerInstallment();
    }

    public double getNextInstallmentPrincipal() {
        return getDelayedInstallmentPrincipal() + (getRemainingInstallmentCount() >= 0 ? getPrincipalPerInstallment() : 0);
    }

    public double getNextInstallmentInterest() {
        return getDelayedInstallmentInterest() + (getRemainingInstallmentCount() >= 0 ? getInterestPerInstallment() : 0);
    }

    public double getNextInstallmentTotal() {
        return getNextInstallmentPrincipal() + getNextInstallmentInterest();
    }

    public int getTimeSincePreviousInstallment() {
        return getPassedTerm() - (getPassedInstallmentCount() * installmentPeriod);
    }

    public int getTimeBeforeNextInstallment() {
        return installmentPeriod - getTimeSincePreviousInstallment();
    }

    public int getPreviousInstallmentTime() {
        return previousInstallmentTime;
    }

    public int getNextInstallmentTime() {
        return nextInstallmentTime;
    }

    public String toShortString() {
        return loanAsShortString;
    }

    @Override
    public String toString() {
        return loanAsString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanDetails that = (LoanDetails) o;
        return originalTerm == that.originalTerm && remainingTerm == that.remainingTerm && installmentPeriod == that.installmentPeriod && delayedInstallmentCount == that.delayedInstallmentCount && previousInstallmentTime == that.previousInstallmentTime && nextInstallmentTime == that.nextInstallmentTime && Double.compare(that.totalInvestment, totalInvestment) == 0 && Double.compare(that.interestPerInstallment, interestPerInstallment) == 0 && Double.compare(that.capital, capital) == 0 && Double.compare(that.paidInterest, paidInterest) == 0 && Double.compare(that.remainingInterest, remainingInterest) == 0 && Double.compare(that.remainingPrincipal, remainingPrincipal) == 0 && Double.compare(that.paidPrincipal, paidPrincipal) == 0 && Objects.equals(loanAsShortString, that.loanAsShortString) && Objects.equals(loanAsString, that.loanAsString) && Objects.equals(id, that.id) && Objects.equals(borrowerName, that.borrowerName) && Objects.equals(category, that.category) && status == that.status && Objects.equals(statusTimes, that.statusTimes) && Objects.equals(investmentByLenderName, that.investmentByLenderName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanAsShortString, loanAsString, id, borrowerName, category, status, statusTimes, investmentByLenderName, originalTerm, remainingTerm, installmentPeriod, delayedInstallmentCount, previousInstallmentTime, nextInstallmentTime, totalInvestment, interestPerInstallment, capital, paidInterest, remainingInterest, remainingPrincipal, paidPrincipal);
    }
}
