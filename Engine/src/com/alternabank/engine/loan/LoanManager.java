package com.alternabank.engine.loan;

import com.alternabank.engine.account.AbstractDepositAccount;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.state.LoanManagerState;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.util.*;

public class LoanManager implements TimeAdvancementListener {

    private final Set<String> availableCategories = new HashSet<>();
    private final Map<String, Loan> loansByID = new HashMap<>();
    private final List<UnilateralTransactionListener> unilateralTransactionListeners = new LinkedList<>();
    private final List<BilateralTransactionListener> bilateralTransactionListeners = new LinkedList<>();

    private static LoanManager instance = null;

    public static LoanManager getInstance() {
        if(instance == null)
            instance = new LoanManager();
        return instance;
    }

    public LoanManagerState createLoanManagerState() {
        return new LoanManagerState(availableCategories, loansByID, unilateralTransactionListeners, bilateralTransactionListeners);
    }

    public void restoreLoanManager(LoanManagerState loanManagerState) {
        this.availableCategories.clear();
        this.availableCategories.addAll(loanManagerState.getAvailableCategories());
        this.loansByID.clear();
        this.loansByID.putAll(loanManagerState.getLoansByID());
        this.unilateralTransactionListeners.clear();
        this.unilateralTransactionListeners.addAll(loanManagerState.getUnilateralTransactionListeners());
        this.bilateralTransactionListeners.clear();
        this.bilateralTransactionListeners.addAll(loanManagerState.getBilateralTransactionListeners());
    }

    private LoanManager() {
        TimeManager.getInstance().addTimeAdvancementListener(this);
    }

    public Loan getLoan(String id) {
        return loansByID.get(id);
    }

    public Map<String, Loan> getLoansByID() {
        return Collections.unmodifiableMap(loansByID);
    }

    public boolean loanExists(String id) {
        return loansByID.containsKey(id);
    }

    public boolean validateInvestmentRequest(Investment.Request request) {
        return loansByID.keySet().containsAll(request.getChosenLoanIDs()) &&
                request.getTotal() >= Investment.MINIMUM_TOTAL &&
                request.getTotal() <= CustomerManager.getInstance().getCustomersByName().get(request.getLenderName()).getAccount().getBalance() &&
                (request.getMinimumInterestRate() == Investment.DEFAULT_VALUE || request.getMinimumInterestRate() > Investment.INTEREST_RATE_LOWER_BOUND) &&
                (request.getMinimumInterestPerTimeUnit() == Investment.DEFAULT_VALUE || request.getMinimumInterestPerTimeUnit() > Investment.INTEREST_PER_TIME_UNIT_LOWER_BOUND) &&
                (request.getMinimumLoanTerm() == Investment.DEFAULT_VALUE || request.getMinimumLoanTerm() > 0) &&
                (request.getMaximumLoanOwnershipPercentage() == Investment.DEFAULT_VALUE || (request.getMaximumLoanOwnershipPercentage() < Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_UPPER_BOUND && request.getMaximumLoanOwnershipPercentage() > Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_LOWER_BOUND)) &&
                (request.getMaximumBorrowerActiveLoans() == Investment.DEFAULT_VALUE || request.getMaximumBorrowerActiveLoans() >= Investment.MINIMUM_MAXIMUM_BORROWER_ACTIVE_LOANS);
    }

    public boolean validateLoanRequest(Loan.Request request) {
        return CustomerManager.getInstance().customerExists(request.getBorrowerName()) &&
                availableCategories.contains(request.getCategory()) &&
                request.getCapital() > Loan.CAPITAL_LOWER_BOUND &&
                request.getInstallmentPeriod() >= Loan.MINIMUM_INSTALLMENT_PERIOD &&
                request.getInstallmentPeriod() <= request.getTerm() &&
                request.getInterestRate() >= Loan.MINIMUM_INTEREST &&
                request.getTerm() >= Loan.MINIMUM_TERM &&
                request.getTerm() % request.getInstallmentPeriod() == 0 &&
                !loanExists(request.getID());
    }

    public Set<String> getAvailableCategories() {
        return Collections.unmodifiableSet(availableCategories);
    }

    public void addCategory(String category) {
        availableCategories.add(category);
    }

    public Loan createLoan(Loan.Request loanRequest) {
        Loan loan = null;

        if(validateLoanRequest(loanRequest)) {
            loan = new BasicLoan(loanRequest);
            loansByID.put(loanRequest.getID(), loan);
        }

        return loan;
    }

    public boolean removeLoan(String id) {
        boolean success = false;
        Loan loan = loansByID.get(id);

        if(loan.getInvestmentByLenderName().isEmpty()) {
            loansByID.remove(id);
            success = true;
        }

        return success;
    }

    public void reset() {
        loansByID.clear();
        availableCategories.clear();
    }

    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        unilateralTransactionListeners.add(listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        bilateralTransactionListeners.add(listener);
    }

    public boolean removeCategory(String category) {
        boolean success = false;

        if(loansByID.values().stream().noneMatch(loan -> loan.getOriginalRequest().getCategory().equals(category))) {
            availableCategories.remove(category);
            success = true;
        }

        return success;
    }

    public Investment createInvestment(Investment.Request investmentRequest) {
        Investment investment = null;

        if(validateInvestmentRequest(investmentRequest)) {
            investment = new BasicInvestment(investmentRequest);
            double maxInvestmentPerLoan = investmentRequest.getTotal() / investmentRequest.getChosenLoanCount();
            investmentRequest.getChosenLoanIDs().stream().map(loanID -> LoanManager.getInstance().getLoan(loanID))
                    .forEach(loan -> loan.investIn(CustomerManager.getInstance().getCustomersByName().get(investmentRequest.getLenderName()),
                            Math.min(loan.getRemainingInvestment(), maxInvestmentPerLoan)));
        }

        return investment;
    }

    @Override
    public void timeAdvanced(TimeAdvancementEvent event) {
        loansByID.values().stream().filter(loan -> loan.getStatus() == Loan.Status.ACTIVE || loan.getStatus() == Loan.Status.RISK)
                .filter(loan -> loan.getPreviousInstallmentTime() == TimeManager.getInstance().getCurrentTime())
                .sorted((loan1, loan2) -> {
                    if(loan1.getStatusTimes().get(Loan.Status.ACTIVE) > loan2.getStatusTimes().get(Loan.Status.ACTIVE))
                        return 1;
                    else if(loan1.getStatusTimes().get(Loan.Status.ACTIVE) < loan2.getStatusTimes().get(Loan.Status.ACTIVE))
                        return -1;
                    else {
                        return Double.compare(loan1.getNextInstallmentTotal(), loan2.getNextInstallmentTotal());
                    }
                })
                .forEach(Loan::executeNextInstallment);
    }

    public class BasicLoan extends AbstractDepositAccount.Deposit implements Loan {

        private final Loan.Request originalRequest;
        private Status status = Status.PENDING;
        private final Map<Status, Integer> statusTimes = new HashMap<>();
        private final Map<String, Double> investmentByLenderName = new HashMap<>();
        private int delayedInstallmentCount = 0;
        private double paidInterest = 0;
        private double paidPrincipal = 0;

        private BasicLoan(Loan.Request request) {
            new LoanAccount(request.getID()).super(request.getID());
            this.originalRequest = request;
            statusTimes.put(Status.PENDING, TimeManager.getInstance().getCurrentTime());
        }

        @Override
        public Request getOriginalRequest() {
            return originalRequest;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public Map<String, Double> getInvestmentByLenderName() {
            return Collections.unmodifiableMap(investmentByLenderName);
        }

        @Override
        public Map<Status, Integer> getStatusTimes() {
            return Collections.unmodifiableMap(statusTimes);
        }

        @Override
        public int getPassedTerm() {
            return TimeManager.getInstance().getCurrentTime() - statusTimes.getOrDefault(Status.ACTIVE, 0);
        }

        @Override
        public int getRemainingTerm() {
            return originalRequest.getTerm() - getPassedTerm();
        }

        @Override
        public int getPassedInstallmentCount() {
            return getPassedTerm() / originalRequest.getInstallmentPeriod();
        }

        @Override
        public int getRemainingInstallmentCount() {
            return originalRequest.getInstallmentCount() - getPassedInstallmentCount();
        }

        @Override
        public double getPaidPrincipal() {
            return getRemainingPrincipal();
        }

        @Override
        public double getPaidInterest() {
            return paidInterest;
        }

        @Override
        public double getPaidTotal() {
            return paidInterest + paidPrincipal;
        }

        @Override
        public double getRemainingPrincipal() {
            return originalRequest.getCapital() - paidPrincipal;
        }

        @Override
        public double getRemainingInterest() {
            return originalRequest.getTotalInterest() - paidInterest;
        }

        @Override
        public double getRemainingTotal() {
            return getRemainingInterest() + getRemainingPrincipal();
        }

        @Override
        public double getTotalInvestment() {
            return investmentByLenderName.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        @Override
        public double getRemainingInvestment() {
            return originalRequest.getCapital() - getTotalInvestment();
        }

        @Override
        public double getDelayedInstallmentPrincipal() {
            return delayedInstallmentCount * originalRequest.getPrincipalPerInstallment();
        }

        @Override
        public double getDelayedInstallmentInterest() {
            return delayedInstallmentCount * originalRequest.getInterestPerInstallment();
        }

        @Override
        public int getDelayedInstallmentCount() {
            return delayedInstallmentCount;
        }

        @Override
        public double getDelayedInstallmentTotal() {
            return getDelayedInstallmentPrincipal() + getDelayedInstallmentInterest();
        }

        @Override
        public double getNextInstallmentPrincipal() {
            return getDelayedInstallmentPrincipal() + (getRemainingInstallmentCount() >= 0 ? originalRequest.getPrincipalPerInstallment() : 0);
        }

        @Override
        public double getNextInstallmentInterest() {
            return getDelayedInstallmentInterest() + (getRemainingInstallmentCount() >= 0 ? originalRequest.getInterestPerInstallment() : 0);
        }

        @Override
        public double getNextInstallmentTotal() {
            return getNextInstallmentPrincipal() + getNextInstallmentInterest();
        }

        @Override
        public double getInvestmentInterest(double investment) {
            return investment * originalRequest.getInterestRate();
        }

        @Override
        public int getTimeSincePreviousInstallment() {
            return getPassedTerm() - (getPassedInstallmentCount() * originalRequest.getInstallmentPeriod());
        }

        @Override
        public int getTimeBeforeNextInstallment() {
            return originalRequest.getInstallmentPeriod() - getTimeSincePreviousInstallment();
        }

        @Override
        public int getPreviousInstallmentTime() {
            return TimeManager.getInstance().getCurrentTime() - getTimeSincePreviousInstallment();
        }

        @Override
        public int getNextInstallmentTime() {
            return TimeManager.getInstance().getCurrentTime() + getTimeBeforeNextInstallment();
        }

        private String getInvestmentByLenderNameAsString() {
            StringBuilder stringBuilder = new StringBuilder();
            investmentByLenderName.forEach((name, total) -> stringBuilder.append(System.lineSeparator()).append(
                    String.format("\t\tLender: %s (Investment Total: %.2f)", name, total)));
            return stringBuilder.toString();
        }

        private String getPendingDataAsString() {
            return String.format("\t\tTotal Investment: %.2f (Remaining: %.2f)",
                    getTotalInvestment(), getRemainingInvestment());
        }

        private String getActiveStatusTimeAsString() {
            return String.format("\t\tActive Since: %s %d",
                    TimeManager.getInstance().getTimeUnitName(), statusTimes.get(Status.ACTIVE));
        }

        private String getNextInstallmentDataAsString() {
            return String.format("\t\tNext Installment On: %s %d (Total: %.2f)",
                    TimeManager.getInstance().getTimeUnitName(), getNextInstallmentTime(), getNextInstallmentTotal());
        }

        private String getLedgerAsString() {
            return "\t\t" + getAccount().getLedger().toString().replace(System.lineSeparator(), System.lineSeparator() + "\t\t\t");
        }

        private String getPrincipalAsString() {
            return String.format("\t\tPrincipal Paid: %.2f (Remaining: %.2f)", paidPrincipal, getRemainingPrincipal());
        }

        private String getInterestAsString() {
            return String.format("\t\tInterest Paid: %.2f (Remaining: %.2f)", paidInterest, getRemainingInterest());
        }

        private String getDelayedInstallmentsAsString() {
            return String.format("\t\tDelayed Installments: %d (Total: %.2f)", getDelayedInstallmentCount(), getDelayedInstallmentTotal());
        }

        private String getStartAndEndTimesAsString() {
            return String.format("\t\tStart Time: %s %d" + System.lineSeparator()
                                + "\t\tEnd Time: %s %d",
                    TimeManager.getInstance().getTimeUnitName(), statusTimes.get(Status.ACTIVE),
                    TimeManager.getInstance().getTimeUnitName(), statusTimes.get(Status.FINISHED));
        }

        private String getStringHeader() {

            return String.format(
                    "%s" + System.lineSeparator()
                            + "\tStatus: %s",
                    originalRequest, status) + System.lineSeparator() + "\tADDITIONAL INFORMATION:";
        }

        public String toShortString() {
            StringBuilder stringBuilder = new StringBuilder(getStringHeader());

            switch (status) {
                case PENDING:
                    stringBuilder.append(System.lineSeparator()).append(getPendingDataAsString());
                    break;
                case ACTIVE:
                    stringBuilder.append(System.lineSeparator()).append(getNextInstallmentDataAsString());
                    break;
                case RISK:
                    stringBuilder.append(System.lineSeparator()).append(getDelayedInstallmentsAsString());
                    break;
                case FINISHED:
                    stringBuilder.append(System.lineSeparator()).append(getStartAndEndTimesAsString());
                    break;
            }

            return stringBuilder.toString();
        }

        @Override
        public LoanDetails toLoanDetails() {
            return new LoanDetails(this);
        }

        @Override
        public String toString() {

            StringBuilder stringBuilder = new StringBuilder(getStringHeader());

            switch (status) {
                case PENDING:
                    stringBuilder.append(getInvestmentByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPendingDataAsString());
                    break;
                case ACTIVE:
                    stringBuilder.append(getInvestmentByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getActiveStatusTimeAsString());
                    stringBuilder.append(System.lineSeparator()).append(getNextInstallmentDataAsString());
                    stringBuilder.append(System.lineSeparator()).append(getLedgerAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPrincipalAsString());
                    stringBuilder.append(System.lineSeparator()).append(getInterestAsString());
                    break;
                case RISK:
                    stringBuilder.append(getInvestmentByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getActiveStatusTimeAsString());
                    stringBuilder.append(System.lineSeparator()).append(getNextInstallmentDataAsString());
                    stringBuilder.append(System.lineSeparator()).append(getLedgerAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPrincipalAsString());
                    stringBuilder.append(System.lineSeparator()).append(getInterestAsString());
                    stringBuilder.append(System.lineSeparator()).append(getDelayedInstallmentsAsString());
                    break;
                case FINISHED:
                    stringBuilder.append(getInvestmentByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPendingDataAsString());
                    stringBuilder.append(System.lineSeparator()).append(getStartAndEndTimesAsString());
                    stringBuilder.append(System.lineSeparator()).append(getLedgerAsString());
                    break;
            }

            return stringBuilder.toString();
        }

        @Override
        public void investIn(CustomerManager.Customer customer, double total) {
            if (status == Status.PENDING) {
                total = total > getRemainingInvestment() ? getRemainingInvestment() : total;
                investmentByLenderName.put(customer.getName(), investmentByLenderName.getOrDefault(customer.getName(), 0.0) + total);
                customer.getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), total, 0);
                if (getTotalInvestment() >= originalRequest.getCapital()) {
                    status = Status.ACTIVE;
                    statusTimes.put(Status.ACTIVE, TimeManager.getInstance().getCurrentTime());
                    getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, CustomerManager.getInstance().getCustomersByName().get(originalRequest.getBorrowerName()).getAccount(), originalRequest.getCapital(), 0);
                }
            }
        }

        @Override
        public void executeNextInstallment() {
            Transaction.Record.Bilateral record = CustomerManager.getInstance().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), getNextInstallmentPrincipal(), getNextInstallmentInterest());
            if(record.getStatus() == Transaction.Status.FAILED && getRemainingInstallmentCount() >= 0) {
                delayedInstallmentCount++;
                if(status != Status.RISK) {
                    status = Status.RISK;
                    statusTimes.put(Status.RISK, TimeManager.getInstance().getCurrentTime());
                }
            }
            else if(record.getStatus() == Transaction.Status.SUCCESSFUL){
                paidPrincipal += getNextInstallmentPrincipal();
                paidInterest += getNextInstallmentInterest();
                if(status == Status.RISK) {
                    status = Status.ACTIVE;
                    delayedInstallmentCount = 0;
                }
                if(getPaidTotal() == originalRequest.getTotal()) {
                    status = Status.FINISHED;
                    statusTimes.put(Status.FINISHED, TimeManager.getInstance().getCurrentTime());
                    investmentByLenderName.keySet().forEach(name -> getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                            CustomerManager.getInstance().getCustomersByName().get(name).getAccount(), investmentByLenderName.get(name), getInvestmentInterest(investmentByLenderName.get(name))));
                }
            }
        }
    }

    public static class BasicInvestment implements Investment {

        Request originalRequest;

        private BasicInvestment(Investment.Request request) {
            this.originalRequest = request;
        }

        @Override
        public Request getOriginalRequest() {
            return originalRequest;
        }
    }

    public class LoanAccount extends AbstractDepositAccount {

        private LoanAccount(String loanID) {
            super(loanID, loanID);
        }

        @Override
        protected List<UnilateralTransactionListener> getUnilateralTransactionListenerList() {
            return unilateralTransactionListeners;
        }

        @Override
        protected List<BilateralTransactionListener> getBilateralTransactionListenerList() {
            return bilateralTransactionListeners;
        }

    }

}
