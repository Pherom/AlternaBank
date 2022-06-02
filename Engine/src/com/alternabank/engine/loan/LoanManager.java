package com.alternabank.engine.loan;

import com.alternabank.engine.account.AbstractDepositAccount;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;
import com.alternabank.engine.loan.event.PaymentDueEvent;
import com.alternabank.engine.loan.event.listener.LoanStatusUpdateListener;
import com.alternabank.engine.loan.event.listener.PaymentDueListener;
import com.alternabank.engine.loan.notification.PaymentNotification;
import com.alternabank.engine.loan.state.LoanManagerState;
import com.alternabank.engine.loan.task.InvestmentTask;
import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.Transaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.user.Admin;
import com.alternabank.engine.user.UserManager;

import javax.swing.event.EventListenerList;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoanManager implements TimeAdvancementListener {

    private final Admin admin;
    private final Set<String> availableCategories = new HashSet<>();
    private final Map<String, Loan> loansByID = new HashMap<>();
    private final EventListenerList eventListeners = new EventListenerList();

    public LoanManagerState createLoanManagerState() {
        return new LoanManagerState(availableCategories, loansByID);
    }

    public void restoreLoanManager(LoanManagerState loanManagerState) {
        this.availableCategories.clear();
        this.availableCategories.addAll(loanManagerState.getAvailableCategories());
        this.loansByID.clear();
        this.loansByID.putAll(loanManagerState.getLoansByID());
    }

    public LoanManager(Admin admin) {
        this.admin = admin;
        admin.getTimeManager().addTimeAdvancementListener(this);
    }

    public Loan getLoan(String id) {
        return loansByID.get(id);
    }

    public Map<String, Loan> getLoansByID() {
        return loansByID;
    }

    public boolean loanExists(String id) {
        return loansByID.containsKey(id);
    }

    public boolean validateInvestmentRequest(Investment.Request request) {
        return loansByID.keySet().containsAll(request.getChosenLoanIDs()) &&
                request.getTotal() >= Investment.MINIMUM_TOTAL &&
                request.getTotal() <= admin.getCustomerManager().getCustomersByName().get(request.getLenderName()).getAccount().getBalance() &&
                (request.getMinimumInterestRate() >= Investment.MINIMUM_INTEREST_RATE) &&
                (request.getMinimumInterest() >= Investment.MINIMUM_INTEREST) &&
                (request.getMinimumLoanTerm() >= 0) &&
                (request.getMaximumLoanOwnershipPercentage() <= Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX && request.getMaximumLoanOwnershipPercentage() >= Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MIN) &&
                (request.getMaximumBorrowerActiveLoans() >= Investment.MAXIMUM_BORROWER_ACTIVE_LOANS_MIN);
    }

    public boolean validateLoanRequest(Loan.Request request) {
        return admin.getCustomerManager().customerExists(request.getBorrowerName()) &&
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
        return availableCategories;
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
        eventListeners.add(UnilateralTransactionListener.class, listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        eventListeners.add(BilateralTransactionListener.class, listener);
    }

    public void addLoanStatusUpdateListener(LoanStatusUpdateListener listener) {
        eventListeners.add(LoanStatusUpdateListener.class, listener);
    }

    public void addPaymentDueListener(PaymentDueListener listener) {
        eventListeners.add(PaymentDueListener.class, listener);
    }

    public boolean removeCategory(String category) {
        boolean success = false;

        if(loansByID.values().stream().noneMatch(loan -> loan.getOriginalRequest().getCategory().equals(category))) {
            availableCategories.remove(category);
            success = true;
        }

        return success;
    }

    public void postInvestmentRequest(Consumer<InvestmentTask> uiBinder, String lenderName, double investmentTotal, double minimumInterest, int maximumLoanOwnershipPercentage, int minimumLoanTerm, int maximumBorrowerActiveLoans, Set<String> categoriesOfInterest, Collection<String> loansToInvestIn) {
        InvestmentTask task = new InvestmentTask(lenderName, investmentTotal, minimumInterest, maximumLoanOwnershipPercentage, minimumLoanTerm, maximumBorrowerActiveLoans, categoriesOfInterest, loansToInvestIn);
        uiBinder.accept(task);
        new Thread(task).start();
    }

    public Investment createInvestment(Investment.Request investmentRequest) {
        Investment investment = null;

        if(validateInvestmentRequest(investmentRequest)) {
            investment = new BasicInvestment(investmentRequest);
            Map<String, Double> maxInvestmentByChosenLoanID = investmentRequest.getChosenLoanIDs().stream().map(loanID -> admin.getLoanManager().getLoan(loanID))
                            .collect(Collectors.toMap(loan -> loan.getAccount().getID(), loan -> loan.getOriginalRequest().getCapital() * (investmentRequest.getMaximumLoanOwnershipPercentage() / 100.0)));

            List<Map.Entry<String, Double>> maxInvestmentByChosenLoanIdSortedEntrySet = maxInvestmentByChosenLoanID.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

            double remainingInvestmentTotal = investmentRequest.getTotal();

            for (int i = 0; i < investmentRequest.getChosenLoanCount(); i++) {
                Loan loan = admin.getLoanManager().getLoan(maxInvestmentByChosenLoanIdSortedEntrySet.get(i).getKey());
                double loanMaxInvestment = maxInvestmentByChosenLoanIdSortedEntrySet.get(i).getValue();
                double averagedInvestment = remainingInvestmentTotal / (investmentRequest.getChosenLoanCount() - i);
                double finalLoanInvestment = Math.min(averagedInvestment, loanMaxInvestment);
                loan.investIn(admin.getCustomerManager().getCustomersByName().get(investmentRequest.getLenderName()
                ), finalLoanInvestment);
                remainingInvestmentTotal -= finalLoanInvestment;
            }
        }
        return investment;
    }

    @Override
    public void timeAdvanced(TimeAdvancementEvent event) {
        loansByID.values().stream().filter(loan -> loan.getStatus() == Loan.Status.ACTIVE || loan.getStatus() == Loan.Status.RISK).forEach(loan -> {
            int currentTime = admin.getTimeManager().getCurrentTime();
            if (loan.getPreviousInstallmentTime() == currentTime) {
                PaymentNotification paymentNotification = new PaymentNotification(loan.getAccount().getID(), loan.getPreviousInstallmentTime(), loan.getAccumulatedDebtPrincipal(), loan.getAccumulatedDebtInterest());
                loan.addPaymentNotification(paymentNotification);
                Arrays.stream(eventListeners.getListeners(PaymentDueListener.class)).forEach(listener -> listener.paymentDue(new PaymentDueEvent(paymentNotification, loan.toLoanDetails())));
            }
            else if (currentTime == loan.getPreviousInstallmentTime() + 1 && loan.getPaidTotal() < loan.getRequiredTotal())
            {
                if (loan.getStatus() == Loan.Status.ACTIVE)
                    loan.setStatus(Loan.Status.RISK);
                loan.incrementDelayedInstallmentCount();
            }
        });
    }

    public Set<LoanDetails> getLoanDetails() {
        return loansByID.values().stream().map(Loan::toLoanDetails).collect(Collectors.toSet());
    }

    public Set<String> getLoanCategories() {
        return availableCategories;
    }

    public int getPostedLoanCountOfCustomerWithMostPostedLoans() {
        return admin.getCustomerManager().getCustomersByName().values().stream()
                .mapToInt(customer -> (int) customer.getPostedLoansIDs().stream()
                        .filter(loanID -> {
                            Loan loan = loansByID.get(loanID);
                                return loan.getStatus() == Loan.Status.ACTIVE ||
                                        loan.getStatus() == Loan.Status.PENDING ||
                                        loan.getStatus() == Loan.Status.RISK;
                        }).count()).max().getAsInt();
    }

    public class BasicLoan extends AbstractDepositAccount.Deposit implements Loan {

        private final Loan.Request originalRequest;
        private Status status = Status.PENDING;
        private final Map<Status, Integer> statusTimes = new HashMap<>();
        private final Map<String, Double> investmentByLenderName = new HashMap<>();
        private int delayedInstallmentCount = 0;
        private double paidInterest = 0;
        private double paidPrincipal = 0;
        private final List<PaymentNotification> paymentNotifications = new LinkedList<>();

        private BasicLoan(Loan.Request request) {
            new LoanAccount(request.getID()).super(request.getID());
            this.originalRequest = request;
            statusTimes.put(Status.PENDING, admin.getTimeManager().getCurrentTime());
        }

        @Override
        public void addPaymentNotification(PaymentNotification paymentNotification) {
            paymentNotifications.add(paymentNotification);
        }

        @Override
        public List<PaymentNotification> getPaymentNotifications() {
            return Collections.unmodifiableList(paymentNotifications);
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
            return investmentByLenderName;
        }

        @Override
        public Map<Status, Integer> getStatusTimes() {
            return statusTimes;
        }

        @Override
        public int getPassedTerm() {
            return admin.getTimeManager().getCurrentTime() - statusTimes.getOrDefault(Status.ACTIVE, 0);
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
            return paidPrincipal;
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
        public double getRequiredPrincipal() {
            return originalRequest.getPrincipalPerInstallment() * getPassedInstallmentCount();
        }

        @Override
        public double getRequiredInterest() {
            return originalRequest.getInterestPerInstallment() * getPassedInstallmentCount();
        }

        @Override
        public double getRequiredTotal() {
            return getRequiredPrincipal() + getRequiredInterest();
        }

        @Override
        public double getAccumulatedDebtPrincipal() {
            return getRequiredPrincipal() - paidPrincipal;
        }

        @Override
        public double getAccumulatedDebtInterest() {
            return getRequiredInterest() - paidInterest;
        }

        @Override
        public double getAccumulatedDebtTotal() {
            return getRequiredTotal() - getPaidTotal();
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
        public int getDelayedInstallmentCount() {
            return delayedInstallmentCount;
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
        public Optional<Integer> getTimeBeforeNextInstallment() {
            Optional<Integer> timeBeforeNextInstallment = Optional.empty();
            if(status != Status.FINISHED)
                timeBeforeNextInstallment = Optional.of(originalRequest.getInstallmentPeriod() - getTimeSincePreviousInstallment());
            return timeBeforeNextInstallment;
        }

        @Override
        public int getPreviousInstallmentTime() {
            return admin.getTimeManager().getCurrentTime() - getTimeSincePreviousInstallment();
        }

        @Override
        public Optional<Integer> getNextInstallmentTime() {
            return getTimeBeforeNextInstallment().isPresent() ? Optional.of(admin.getTimeManager().getCurrentTime() + getTimeBeforeNextInstallment().get()) : Optional.empty();
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
                    admin.getTimeManager().getTimeUnitName(), statusTimes.get(Status.ACTIVE));
        }

        private String getNextInstallmentDataAsString() {
            Optional<Integer> nextInstallmentTime = getNextInstallmentTime();
            String nextInstallmentTimeAsString = nextInstallmentTime.isPresent() ? String.format("%s %d", admin.getTimeManager().getTimeUnitName(), nextInstallmentTime.get()) : "N/A";
            return String.format("\t\tNext Installment On: %s (Total: %.2f)",
                    nextInstallmentTimeAsString, getAccumulatedDebtTotal());
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
            return String.format("\t\tDelayed Installments: %d (Total: %.2f)", getDelayedInstallmentCount(), getAccumulatedDebtTotal());
        }

        private String getStartAndEndTimesAsString() {
            return String.format("\t\tStart Time: %s %d" + System.lineSeparator()
                                + "\t\tEnd Time: %s %d",
                    admin.getTimeManager().getTimeUnitName(), statusTimes.get(Status.ACTIVE),
                    admin.getTimeManager().getTimeUnitName(), statusTimes.get(Status.FINISHED));
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BasicLoan basicLoan = (BasicLoan) o;
            return Objects.equals(originalRequest, basicLoan.originalRequest);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), originalRequest);
        }

        @Override
        public void investIn(CustomerManager.Customer customer, double total) {
            if (status == Status.PENDING) {
                total = total > getRemainingInvestment() ? getRemainingInvestment() : total;
                investmentByLenderName.put(customer.getName(), investmentByLenderName.getOrDefault(customer.getName(), 0.0) + total);
                customer.getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), total, 0);
                if (getTotalInvestment() >= originalRequest.getCapital()) {
                    setStatus(Status.ACTIVE);
                    getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, admin.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName()).getAccount(), originalRequest.getCapital(), 0);
                }
            }
        }

        @Override
        public void executeAccumulatedDebtPayment() {
            Transaction.Record.Bilateral record = admin.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), getAccumulatedDebtPrincipal(), getAccumulatedDebtInterest());
            if (record.getStatus() == Transaction.Status.SUCCESSFUL) {
                paidPrincipal += getAccumulatedDebtPrincipal();
                paidInterest += getAccumulatedDebtInterest();
                if (getRemainingTotal() == 0) {
                    setStatus(Status.FINISHED);
                    investmentByLenderName.keySet().forEach(name -> getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                            admin.getCustomerManager().getCustomersByName().get(name).getAccount(), investmentByLenderName.get(name), getInvestmentInterest(investmentByLenderName.get(name))));
                }
                else setStatus(Status.ACTIVE);
            }
        }

        @Override
        public void executeRemainingTotalPayment() {
            Transaction.Record.Bilateral record = admin.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), getRemainingPrincipal(), getRemainingInterest());
            if (record.getStatus() == Transaction.Status.SUCCESSFUL) {
                paidPrincipal += getRemainingPrincipal();
                paidInterest += getRemainingInterest();
                setStatus(Status.FINISHED);
                investmentByLenderName.keySet().forEach(name -> getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                        admin.getCustomerManager().getCustomersByName().get(name).getAccount(), investmentByLenderName.get(name), getInvestmentInterest(investmentByLenderName.get(name))));
            }
        }

        @Override
        public void executeRiskPayment(double paymentTotal) {
            double paymentInterest = paymentTotal * originalRequest.getInterestRate();
            double paymentPrincipal = paymentTotal - paymentInterest;
            Transaction.Record.Bilateral record = admin.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), paymentPrincipal, paymentInterest);
            if (record.getStatus() == Transaction.Status.SUCCESSFUL) {
                paidPrincipal += paymentPrincipal;
                paidInterest += paymentInterest;
                if (getRemainingTotal() == 0) {
                    setStatus(Status.FINISHED);
                    investmentByLenderName.keySet().forEach(name -> getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                            admin.getCustomerManager().getCustomersByName().get(name).getAccount(), investmentByLenderName.get(name), getInvestmentInterest(investmentByLenderName.get(name))));
                }
                else if (getAccumulatedDebtTotal() == 0) {
                    setStatus(Status.ACTIVE);
                }
            }
        }

        @Override
        public void setStatus(Status status) {
            Loan.Status previousStatus = this.status;
            this.status = status;
            if (!(status == Status.ACTIVE && statusTimes.containsKey(Status.ACTIVE)))
                statusTimes.put(status, UserManager.getInstance().getAdmin().getTimeManager().getCurrentTime());
            Arrays.stream(eventListeners.getListeners(LoanStatusUpdateListener.class)).forEach(listener -> listener.statusUpdated(new LoanStatusUpdateEvent(previousStatus, toLoanDetails())));
        }

        @Override
        public void incrementDelayedInstallmentCount() {
            delayedInstallmentCount++;
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
        public EventListenerList getEventListeners() {
            return eventListeners;
        }
    }

}
