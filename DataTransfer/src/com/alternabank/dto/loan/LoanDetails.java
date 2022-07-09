package com.alternabank.dto.loan;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.loan.notification.PaymentNotification;
import com.alternabank.dto.loan.status.LoanStatusData;

import java.util.*;

public class LoanDetails {

    private final String loanAsShortString;
    private final String loanAsString;
    private final String id;
    private final String borrowerName;
    private final String category;
    private final LoanStatusData status;
    private final Map<LoanStatusData, Integer> statusTimes;
    private final Map<String, List<InvestmentDetails>> investmentsByLenderName;
    private final AccountDetails accountDetails;
    private final int originalTerm;
    private final int passedTerm;
    private final int installmentPeriod;
    private final int delayedInstallmentCount;
    private final OptionalInt previousInstallmentTime;

    private final int borrowerActiveLoanCount;
    private final Optional<Integer> nextInstallmentTime;
    private final double totalInvestment;
    private final double interestPerInstallment;
    private final double capital;
    private final double paidInterest;
    private final double paidPrincipal;
    private final List<PaymentNotification> paymentNotifications;

    public LoanDetails(String loanAsShortString, String loanAsString, String id, String borrowerName, String category, LoanStatusData status,
                       Map<LoanStatusData, Integer> statusTimes, double totalInvestment, Map<String, List<InvestmentDetails>> investmentsByLenderName,
                       int originalTerm, int passedTerm, int installmentPeriod, int delayedInstallmentCount, OptionalInt previousInstallmentTime,
                       int borrowerActiveLoanCount, Optional<Integer> nextInstallmentTime, double interestPerInstallment, double capital,
                       double paidInterest, double paidPrincipal, AccountDetails accountDetails,
                       List<PaymentNotification> paymentNotifications) {
        this.loanAsShortString = loanAsShortString;
        this.loanAsString = loanAsString;
        this.id = id;
        this.borrowerName = borrowerName;
        this.category = category;
        this.status = status;
        this.statusTimes = statusTimes;
        this.totalInvestment = totalInvestment;
        this.investmentsByLenderName = new HashMap<>(investmentsByLenderName);
        this.originalTerm = originalTerm;
        this.passedTerm = passedTerm;
        this.installmentPeriod = installmentPeriod;
        this.delayedInstallmentCount = delayedInstallmentCount;
        this.previousInstallmentTime = previousInstallmentTime;
        this.borrowerActiveLoanCount = borrowerActiveLoanCount;
        this.nextInstallmentTime = nextInstallmentTime;
        this.interestPerInstallment = interestPerInstallment;
        this.capital = capital;
        this.paidInterest = paidInterest;
        this.paidPrincipal = paidPrincipal;
        this.accountDetails = accountDetails;
        this.paymentNotifications = paymentNotifications;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public int getBorrowerActiveLoanCount() {
        return borrowerActiveLoanCount;
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

    public LoanStatusData getStatus() {
        return status;
    }

    public Map<LoanStatusData, Integer> getStatusTimes() {
        return statusTimes;
    }

    public Map<String, List<InvestmentDetails>> getInvestmentsByLenderName() {
        return investmentsByLenderName;
    }

    public int getRemainingTerm() {
        return originalTerm - passedTerm;
    }

    public int getPassedTerm() {
        return passedTerm;
    }

    public double getPaidPrincipal() {
        return paidPrincipal;
    }

    public double getPaidInterest() {
        return paidInterest;
    }

    public double getPaidTotal() {
        return paidInterest + paidPrincipal;
    }

    public double getRemainingInterest() {
        return getTotalInterest() - paidInterest;
    }

    public double getRemainingPrincipal() {
        return getCapital() - paidPrincipal;
    }

    public double getRemainingTotal() {
        return getRemainingInterest() + getRemainingPrincipal();
    }

    public double getRequiredPrincipal() {
        return getPrincipalPerInstallment() * (getPassedTerm() / installmentPeriod);
    }

    public double getRequiredInterest() {
        return getInterestPerInstallment() * (getPassedTerm() / installmentPeriod);
    }

    public double getRequiredTotal() {
        return getRequiredPrincipal() + getRequiredInterest();
    }

    public double getAccumulatedDebtTotal() {
        return getRequiredTotal() - getPaidTotal();
    }

    public double getTotalInvestment() {
        return totalInvestment;
    }

    public double getRemainingInvestment() {
        return status == LoanStatusData.PENDING ? capital - totalInvestment : 0;
    }

    public double getLenderTotalInvestment(String lenderName) {
        return investmentsByLenderName.get(lenderName).stream().mapToDouble(InvestmentDetails::getInvestmentTotal).sum();
    }

    public double getLenderOwnershipRate(String lenderName) {
        return capital / getLenderTotalInvestment(lenderName);
    }
    public double getLenderRemainingPrincipalPortion(String lenderName) {
        return getRemainingPrincipal() * getLenderOwnershipRate(lenderName);
    }

    public int getDelayedInstallmentCount() {
        return delayedInstallmentCount;
    }

    public OptionalInt getPreviousInstallmentTime() {
        return previousInstallmentTime;
    }

    public Optional<Integer> getNextInstallmentTime() {
        return nextInstallmentTime;
    }

    public String toShortString() {
        return loanAsShortString;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public List<PaymentNotification> getPaymentNotifications() {
        return paymentNotifications;
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
