package com.alternabank.engine.loan;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.dto.LoanDetails;

import java.util.Map;

public interface Loan {

    double CAPITAL_LOWER_BOUND = 0;
    int MINIMUM_INSTALLMENT_PERIOD = 1;
    double MINIMUM_INTEREST = 0;
    double MAXIMUM_INTEREST = 100;
    int MINIMUM_TERM = 1;

    Request getOriginalRequest();

    Status getStatus();

    Map<String, Double> getInvestmentByLenderName();

    Map<Status, Integer> getStatusTimes();

    int getRemainingTerm();

    int getPassedTerm();

    int getPassedInstallmentCount();

    int getRemainingInstallmentCount();

    double getPaidPrincipal();

    double getPaidInterest();

    double getPaidTotal();

    double getRemainingPrincipal();

    double getRemainingInterest();

    double getRemainingTotal();

    double getTotalInvestment();

    double getRemainingInvestment();

    double getDelayedInstallmentPrincipal();

    double getDelayedInstallmentInterest();

    double getDelayedInstallmentTotal();

    double getNextInstallmentPrincipal();

    double getNextInstallmentInterest();

    double getNextInstallmentTotal();

    double getInvestmentInterest(double investment);

    int getDelayedInstallmentCount();

    int getTimeSincePreviousInstallment();

    int getTimeBeforeNextInstallment();

    int getPreviousInstallmentTime();

    int getNextInstallmentTime();

    String toShortString();

    LoanDetails toLoanDetails();

    void investIn(CustomerManager.Customer customer, double total);

    void executeNextInstallment();

    interface Request {

        String getBorrowerName();

        String getCategory();

        double getCapital();

        double getTotalInterest();

        double getTotal();

        int getInstallmentPeriod();

        int getInstallmentCount();

        double getInterestPerInstallment();

        double getInterestPerTimeUnit();

        double getPrincipalPerInstallment();

        double getTotalPerInstallment();

        double getInterestRate();

        int getTerm();

        String getID();

    }

    enum Status {

        PENDING, ACTIVE, RISK, FINISHED

    }
}
