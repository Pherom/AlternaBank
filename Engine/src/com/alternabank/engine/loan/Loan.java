package com.alternabank.engine.loan;

import com.alternabank.engine.account.DepositAccount;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.notification.PaymentNotification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Loan {

    double CAPITAL_LOWER_BOUND = 0;
    int MINIMUM_INSTALLMENT_PERIOD = 1;
    double MINIMUM_INTEREST = 0;
    double MAXIMUM_INTEREST = 100;
    int MINIMUM_TERM = 1;

    void addPaymentNotification(PaymentNotification paymentNotification);

    List<PaymentNotification> getPaymentNotifications();

    Request getOriginalRequest();

    DepositAccount getAccount();

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

    double getRequiredPrincipal();

    double getRequiredInterest();

    double getRequiredTotal();

    double getAccumulatedDebtPrincipal();

    double getAccumulatedDebtInterest();

    double getAccumulatedDebtTotal();

    double getRemainingPrincipal();

    double getRemainingInterest();

    double getRemainingTotal();

    double getTotalInvestment();

    double getRemainingInvestment();

    double getInvestmentInterest(double investment);

    int getDelayedInstallmentCount();

    int getTimeSincePreviousInstallment();

    Optional<Integer> getTimeBeforeNextInstallment();

    int getPreviousInstallmentTime();

    Optional<Integer> getNextInstallmentTime();

    String toShortString();

    LoanDetails toLoanDetails();

    void investIn(CustomerManager.Customer customer, double total);

    void executeAccumulatedDebtPayment();

    void executeRemainingTotalPayment();

    void executeRiskPayment(double paymentTotal);

    void setStatus(Status status);

    void incrementDelayedInstallmentCount();

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
