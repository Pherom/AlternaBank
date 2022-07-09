package com.alternabank.engine.loan;

import com.alternabank.dto.loan.notification.LoanStatusChangeNotification;
import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.engine.account.DepositAccount;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.dto.loan.notification.PaymentNotification;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public interface Loan {

    double CAPITAL_LOWER_BOUND = 0;
    int MINIMUM_INSTALLMENT_PERIOD = 1;
    double MINIMUM_INTEREST = 0;
    double MAXIMUM_INTEREST = 100;
    int MINIMUM_TERM = 1;

    void addPaymentNotification(PaymentNotification paymentNotification);

    void addLoanStatusChangeNotification(LoanStatusChangeNotification loanStatusChangeNotification);

    List<PaymentNotification> getPaymentNotifications();

    List<LoanStatusChangeNotification> getLoanStatusChangeNotifications();

    LoanRequest getOriginalRequest();

    DepositAccount getAccount();

    Status getStatus();

    Map<String, List<Investment>> getInvestmentsByLenderName();

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

    double getLenderTotalInvestment(String lenderName);

    double getLenderOwnershipRate(String lenderName);

    double getLenderRemainingPrincipalPortion(String lenderName);

    double getLenderRemainingInterestPortion(String lenderName);

    double getLenderRemainingTotalPortion(String lenderName);

    double getLenderPaidPrincipalPortion(String lenderName);

    double getLenderPaidInterestPortion(String lenderName);

    double getLenderPaidTotalPortion(String lenderName);

    double getRemainingInvestment();

    double getInvestmentInterest(double investment);

    int getDelayedInstallmentCount();

    OptionalInt getTimeSincePreviousInstallment();

    OptionalInt getTimeBeforeNextInstallment();

    OptionalInt getPreviousInstallmentTime();

    Optional<Integer> getNextInstallmentTime();

    String toShortString();

    LoanDetails toDTO();

    void addInvestment(Investment investment);

    void executeAccumulatedDebtPayment();

    void executeRemainingTotalPayment();

    void executeRiskPayment(double paymentTotal);

    void setStatus(Status status);

    void incrementDelayedInstallmentCount();

    boolean isInstallmentTime();

    enum Status {

        PENDING, ACTIVE, RISK, FINISHED;

        public static LoanStatusData toDTO(Status status) {
            LoanStatusData result = null;
            switch (status) {
                case PENDING:
                    result = LoanStatusData.PENDING;
                    break;
                case ACTIVE:
                    result = LoanStatusData.ACTIVE;
                    break;
                case RISK:
                    result = LoanStatusData.RISK;
                    break;
                case FINISHED:
                    result = LoanStatusData.FINISHED;
                    break;
            }
            return result;
        }

    }
}
