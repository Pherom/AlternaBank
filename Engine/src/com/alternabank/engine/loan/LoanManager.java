package com.alternabank.engine.loan;

import com.alternabank.dto.loan.InvestmentDetails;
import com.alternabank.dto.loan.notification.LoanStatusChangeNotification;
import com.alternabank.dto.loan.request.InvestmentRequest;
import com.alternabank.dto.loan.request.InvestmentRequestBuilder;
import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.dto.transaction.BilateralTransactionRecord;
import com.alternabank.dto.transaction.status.TransactionStatusData;
import com.alternabank.engine.account.AbstractDepositAccount;
import com.alternabank.engine.account.Account;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.customer.Lender;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;
import com.alternabank.engine.loan.event.PaymentDueEvent;
import com.alternabank.engine.loan.event.listener.LoanStatusUpdateListener;
import com.alternabank.engine.loan.event.listener.PaymentDueListener;
import com.alternabank.dto.loan.notification.PaymentNotification;
import com.alternabank.engine.loan.state.LoanManagerState;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.time.event.TimeAdvancementEvent;
import com.alternabank.engine.time.event.TimeReversalEvent;
import com.alternabank.engine.time.event.listener.TimeAdvancementListener;
import com.alternabank.engine.time.event.listener.TimeReversalListener;
import com.alternabank.engine.transaction.BilateralTransaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.Engine;

import javax.swing.event.EventListenerList;
import java.util.*;
import java.util.stream.Collectors;

public class LoanManager implements TimeAdvancementListener {

    private final Engine engine;
    private final List<String> availableCategories = new ArrayList<>();
    private final Map<String, Loan> loansByID = new HashMap<>();
    private final EventListenerList eventListeners = new EventListenerList();

    private final List<Map<String, LoanDetails>> previousLoanDetailsStates = new ArrayList<>();

    private final List<List<String>> previousAvailableCategoriesStates = new ArrayList<>();

    public LoanManagerState createLoanManagerState() {
        return new LoanManagerState(availableCategories, loansByID);
    }

    public void restoreLoanManager(LoanManagerState loanManagerState) {
        this.availableCategories.clear();
        this.availableCategories.addAll(loanManagerState.getAvailableCategories());
        this.loansByID.clear();
        this.loansByID.putAll(loanManagerState.getLoansByID());
    }

    public void saveLoanDetails() {
        int currentTime = engine.getTimeManager().getCurrentTime();
        if (previousLoanDetailsStates.size() == currentTime)
            previousLoanDetailsStates.add(getLoanDetails());
        else previousLoanDetailsStates.set(currentTime, getLoanDetails());
    }

    public void saveAvailableCategories() {
        int currentTime = engine.getTimeManager().getCurrentTime();
        if (previousAvailableCategoriesStates.size() == currentTime)
            previousAvailableCategoriesStates.add(getAvailableCategories());
        else previousAvailableCategoriesStates.set(currentTime, getAvailableCategories());
    }

    public LoanManager(Engine engine) {
        this.engine = engine;
        engine.getTimeManager().addTimeAdvancementListener(this);
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

    public boolean validateInvestmentRequest(InvestmentRequest request) {
        return loansByID.keySet().containsAll(request.getChosenLoanIDs()) &&
                request.getTotal() >= Investment.MINIMUM_TOTAL &&
                request.getTotal() <= engine.getCustomerManager().getCustomersByName().get(request.getLenderName()).getAccount().getBalance() &&
                (request.getMinimumInterestRate() >= Investment.MINIMUM_INTEREST_RATE) &&
                (request.getMinimumInterest() >= Investment.MINIMUM_INTEREST) &&
                (request.getMinimumLoanTerm() >= 0) &&
                (request.getMaximumLoanOwnershipPercentage() <= Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MAX && request.getMaximumLoanOwnershipPercentage() >= Investment.MAXIMUM_LOAN_OWNERSHIP_PERCENTAGE_MIN) &&
                (request.getMaximumBorrowerActiveLoans() >= Investment.MAXIMUM_BORROWER_ACTIVE_LOANS_MIN);
    }

    public boolean validateLoanRequest(LoanRequest request) {
        return engine.getCustomerManager().customerExists(request.getBorrowerName()) &&
                availableCategories.contains(request.getCategory()) &&
                request.getCapital() > Loan.CAPITAL_LOWER_BOUND &&
                request.getInstallmentPeriod() >= Loan.MINIMUM_INSTALLMENT_PERIOD &&
                request.getInstallmentPeriod() <= request.getTerm() &&
                request.getInterestRate() >= Loan.MINIMUM_INTEREST &&
                request.getTerm() >= Loan.MINIMUM_TERM &&
                request.getTerm() % request.getInstallmentPeriod() == 0 &&
                !loanExists(request.getID());
    }

    public List<String> getAvailableCategories() {
        return availableCategories;
    }

    public List<String> getAvailableCategories(int time) {
        return previousAvailableCategoriesStates.get(time);
    }

    public void addCategory(String category) {
        availableCategories.add(category);
    }

    public Loan createLoan(LoanRequest loanRequest) {
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

        if(loan.getInvestmentsByLenderName().isEmpty()) {
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

    public void postInvestmentRequest(InvestmentRequest investmentRequest) {
        Lender lender = engine.getCustomerManager().getCustomersByName().get(investmentRequest.getLenderName());
        lender.postInvestmentRequest(investmentRequest);
    }

    public boolean executeInvestmentRequest(InvestmentRequest investmentRequest) {
        boolean success = validateInvestmentRequest(investmentRequest);
        if(success) {
            Map<String, Double> maxInvestmentByChosenLoanID = investmentRequest.getChosenLoanIDs().stream().map(loanID -> engine.getLoanManager().getLoan(loanID))
                            .collect(Collectors.toMap(loan -> loan.getAccount().getID(), loan -> loan.getOriginalRequest().getCapital() * (investmentRequest.getMaximumLoanOwnershipPercentage() / 100.0)));

            List<Map.Entry<String, Double>> maxInvestmentByChosenLoanIdSortedEntrySet = maxInvestmentByChosenLoanID.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

            double remainingInvestmentTotal = investmentRequest.getTotal();

            for (int i = 0; i < investmentRequest.getChosenLoanCount(); i++) {
                Loan loan = engine.getLoanManager().getLoan(maxInvestmentByChosenLoanIdSortedEntrySet.get(i).getKey());
                double loanMaxInvestment = maxInvestmentByChosenLoanIdSortedEntrySet.get(i).getValue();
                double averagedInvestment = remainingInvestmentTotal / (investmentRequest.getChosenLoanCount() - i);
                double finalLoanInvestment = Math.min(averagedInvestment, loanMaxInvestment);
                Investment investment = new BasicInvestment(investmentRequest, investmentRequest.getChosenLoanIDs().get(i), finalLoanInvestment);
                loan.addInvestment(investment);
                remainingInvestmentTotal -= finalLoanInvestment;
            }
        }
        return success;
    }

    @Override
    public void timeAdvanced(TimeAdvancementEvent event) {
        loansByID.values().stream().filter(loan -> loan.getStatus() == Loan.Status.ACTIVE || loan.getStatus() == Loan.Status.RISK).forEach(loan -> {
            int currentTime = engine.getTimeManager().getCurrentTime();
            if (loan.isInstallmentTime()) {
                PaymentNotification paymentNotification = new PaymentNotification(TimeManager.TIME_UNIT_NAME, loan.getAccount().getID(), currentTime, loan.getAccumulatedDebtPrincipal(), loan.getAccumulatedDebtInterest());
                loan.addPaymentNotification(paymentNotification);
                Arrays.stream(eventListeners.getListeners(PaymentDueListener.class)).forEach(listener -> listener.paymentDue(new PaymentDueEvent(paymentNotification, loan.toDTO())));
            }
            double requiredTotal = loan.getRequiredTotal();
            if (loan.getTimeSincePreviousInstallment().isPresent() && loan.getTimeSincePreviousInstallment().getAsInt() == 1 && loan.getPaidTotal() < (loan.isInstallmentTime() ? requiredTotal - loan.getOriginalRequest().getTotalPerInstallment() : requiredTotal)) {
                if (loan.getStatus() == Loan.Status.ACTIVE)
                    loan.setStatus(Loan.Status.RISK);
                loan.incrementDelayedInstallmentCount();
            }
        });
    }

    public Map<String, LoanDetails> getLoanDetails() {
        return loansByID.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDTO()));
    }

    public Map<String, LoanDetails> getLoanDetails(int time) {
        return previousLoanDetailsStates.get(time);
    }

    public int getPostedLoanCountOfCustomerWithMostPostedLoans() {
        return engine.getCustomerManager().getCustomersByName().values().stream()
                .mapToInt(customer -> (int) customer.getPostedLoanIDs().stream()
                        .filter(loanID -> {
                            Loan loan = loansByID.get(loanID);
                                return loan.getStatus() == Loan.Status.ACTIVE ||
                                        loan.getStatus() == Loan.Status.PENDING ||
                                        loan.getStatus() == Loan.Status.RISK;
                        }).count()).max().getAsInt();
    }

    public boolean postRemainingInvestmentForSale(String lenderName, String loanID) {
        boolean success;
        Loan loan = loansByID.get(loanID);
        success = loan.getStatus() == Loan.Status.ACTIVE;
        if (success) {
            loan.getInvestmentsByLenderName().get(lenderName).forEach(investment -> investment.setForSale(true));
        }
        return success;
    }

    public boolean executeRemainingInvestmentSale(String loanID, String buyerName, String sellerName) {
        boolean success = false;
        Loan loan = loansByID.get(loanID);
        if (loan != null && loan.getStatus() == Loan.Status.ACTIVE) {
            Map<String, List<Investment>> investmentsByLenderName = loan.getInvestmentsByLenderName();
            List<Investment> sellerInvestments = investmentsByLenderName.get(sellerName);
            if (sellerInvestments != null) {
                List<Investment> sellerInvestmentsForSale = sellerInvestments.stream().filter(Investment::getForSale).collect(Collectors.toList());
                if (!sellerInvestmentsForSale.isEmpty()) {
                    double price = loan.getLenderRemainingPrincipalPortion(sellerName);
                    Account buyerAccount = engine.getCustomerManager().getCustomersByName().get(buyerName).getAccount();
                    Account sellerAccount = engine.getCustomerManager().getCustomersByName().get(sellerName).getAccount();
                    BilateralTransactionRecord loanToSellerTransactionRecord = null;
                    if (loan.getPaidTotal() > 0)
                        loanToSellerTransactionRecord = loan.getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, sellerAccount, loan.getLenderPaidPrincipalPortion(sellerName), loan.getLenderPaidInterestPortion(sellerName), engine.getTimeManager().getCurrentTime());
                    if (loanToSellerTransactionRecord == null || loanToSellerTransactionRecord.getStatus() == TransactionStatusData.SUCCESSFUL) {
                        BilateralTransactionRecord buyerToSellerTransactionRecord = buyerAccount.executeTransaction(BilateralTransaction.Type.TRANSFER, sellerAccount, price, 0, engine.getTimeManager().getCurrentTime());
                        loan.getInvestmentsByLenderName().remove(sellerName);
                        if (buyerToSellerTransactionRecord.getStatus() == TransactionStatusData.SUCCESSFUL) {
                            Set<String> loanCategorySet = new HashSet<>();
                            loanCategorySet.add(loan.getOriginalRequest().getCategory());
                            InvestmentRequestBuilder investmentRequestBuilder = new InvestmentRequestBuilder(loanCategorySet, buyerName, price).addLoanToInvestIn(loanID);
                            Investment investment = new BasicInvestment(investmentRequestBuilder.build(), loanID, price);
                            List<Investment> buyerInvestmentsList = investmentsByLenderName.get(buyerName);
                            if (buyerInvestmentsList == null) {
                                buyerInvestmentsList = new ArrayList<>();
                                buyerInvestmentsList.add(investment);
                                investmentsByLenderName.put(buyerName, buyerInvestmentsList);
                            }
                            else {
                                buyerInvestmentsList.add(investment);
                            }
                            success = true;
                        }
                    }
                }
            }
        }
        return success;
    }

    public class BasicLoan extends AbstractDepositAccount.Deposit implements Loan {

        private final LoanRequest originalRequest;
        private Status status = Status.PENDING;
        private final Map<Status, Integer> statusTimes = new HashMap<>();
        private final Map<String, List<Investment>> investmentsByLenderName = new HashMap<>();
        private int delayedInstallmentCount = 0;
        private double paidInterest = 0;
        private double paidPrincipal = 0;
        private final List<PaymentNotification> paymentNotifications = new ArrayList<>();

        private final List<LoanStatusChangeNotification> loanStatusChangeNotifications = new ArrayList<>();

        private BasicLoan(LoanRequest request) {
            new LoanAccount(request.getID()).super(request.getID());
            this.originalRequest = request;
            statusTimes.put(Status.PENDING, engine.getTimeManager().getCurrentTime());
        }

        @Override
        public void addPaymentNotification(PaymentNotification paymentNotification) {
            paymentNotifications.add(paymentNotification);
            CustomerManager.Customer customer = engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName());
            customer.addPaymentNotification(paymentNotification);
        }

        @Override
        public void addLoanStatusChangeNotification(LoanStatusChangeNotification loanStatusChangeNotification) {
            loanStatusChangeNotifications.add(loanStatusChangeNotification);
            CustomerManager.Customer borrower = engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName());
            borrower.addLoanStatusChangeNotification(loanStatusChangeNotification);
            investmentsByLenderName.keySet().forEach( lenderName -> {
                CustomerManager.Customer lender = engine.getCustomerManager().getCustomersByName().get(lenderName);
                lender.addLoanStatusChangeNotification(loanStatusChangeNotification);
            });
        }

        @Override
        public List<PaymentNotification> getPaymentNotifications() {
            return Collections.unmodifiableList(paymentNotifications);
        }

        @Override
        public List<LoanStatusChangeNotification> getLoanStatusChangeNotifications() {
            return Collections.unmodifiableList(loanStatusChangeNotifications);
        }

        @Override
        public LoanRequest getOriginalRequest() {
            return originalRequest;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public Map<String, List<Investment>> getInvestmentsByLenderName() {
            return investmentsByLenderName;
        }

        @Override
        public Map<Status, Integer> getStatusTimes() {
            return statusTimes;
        }

        @Override
        public int getPassedTerm() {
            int passedTerm = status != Status.PENDING ? engine.getTimeManager().getCurrentTime() - statusTimes.get(Status.ACTIVE) : 0;
            passedTerm = Math.min(passedTerm, originalRequest.getTerm());
            return passedTerm;
        }

        @Override
        public int getRemainingTerm() {
            return originalRequest.getTerm() - getPassedTerm();
        }

        @Override
        public int getPassedInstallmentCount() {
            int result = getPassedTerm() / originalRequest.getInstallmentPeriod();
            if (isInstallmentTime())
                result -= 1;
            return result;
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
            return originalRequest.getPrincipalPerInstallment() * (getPassedTerm() / originalRequest.getInstallmentPeriod());
        }

        @Override
        public double getRequiredInterest() {
            return originalRequest.getInterestPerInstallment() * (getPassedTerm() / originalRequest.getInstallmentPeriod());
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
            return investmentsByLenderName.values().stream().mapToDouble(investmentList -> investmentList.stream().mapToDouble(Investment::getInvestmentTotal).sum()).sum();
        }

        @Override
        public double getLenderTotalInvestment(String lenderName) {
            return investmentsByLenderName.get(lenderName).stream().mapToDouble(Investment::getInvestmentTotal).sum();
        }

        @Override
        public double getLenderOwnershipRate(String lenderName) {
            return originalRequest.getCapital() / getLenderTotalInvestment(lenderName);
        }

        @Override
        public double getLenderRemainingPrincipalPortion(String lenderName) {
            return getRemainingPrincipal() * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getLenderRemainingInterestPortion(String lenderName) {
            return getRemainingInterest() * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getLenderRemainingTotalPortion(String lenderName) {
            return getRemainingTotal() * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getLenderPaidPrincipalPortion(String lenderName) {
            return paidPrincipal * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getLenderPaidInterestPortion(String lenderName) {
            return paidInterest * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getLenderPaidTotalPortion(String lenderName) {
            return getPaidTotal() * getLenderOwnershipRate(lenderName);
        }

        @Override
        public double getRemainingInvestment() {
            return status == Status.PENDING ? originalRequest.getCapital() - getTotalInvestment() : 0;
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
        public OptionalInt getTimeBeforeNextInstallment() {
            OptionalInt timeBeforeNextInstallment = OptionalInt.empty();
            OptionalInt timeSincePreviousInstallment = getTimeSincePreviousInstallment();
            if(status != Status.FINISHED && status != Status.PENDING) {
                if (isInstallmentTime()) {
                    if (getAccumulatedDebtTotal() > 0)
                        timeBeforeNextInstallment = OptionalInt.of(0);
                    else timeBeforeNextInstallment = OptionalInt.of(originalRequest.getInstallmentPeriod());
                }
                else if (timeSincePreviousInstallment.isPresent())
                    timeBeforeNextInstallment = OptionalInt.of(originalRequest.getInstallmentPeriod() - timeSincePreviousInstallment.getAsInt());
                else timeBeforeNextInstallment = OptionalInt.of(originalRequest.getInstallmentPeriod());
            }
            return timeBeforeNextInstallment;
        }

        @Override
        public OptionalInt getTimeSincePreviousInstallment() {
            OptionalInt result;
            if (status == Status.PENDING || getPassedInstallmentCount() == 0)
                result = OptionalInt.empty();
            else result = OptionalInt.of(engine.getTimeManager().getCurrentTime() - statusTimes.get(Status.ACTIVE) - (getPassedInstallmentCount() * originalRequest.getInstallmentPeriod()));
            return result;
        }
        @Override
        public OptionalInt getPreviousInstallmentTime() {
            OptionalInt result;
            OptionalInt timeSincePreviousInstallment = getTimeSincePreviousInstallment();
            if (!timeSincePreviousInstallment.isPresent())
                result = OptionalInt.empty();
            else result = OptionalInt.of(engine.getTimeManager().getCurrentTime() - timeSincePreviousInstallment.getAsInt());
            return result;
        }

        @Override
        public Optional<Integer> getNextInstallmentTime() {
            return getTimeBeforeNextInstallment().isPresent() ? Optional.of(engine.getTimeManager().getCurrentTime() + getTimeBeforeNextInstallment().getAsInt()) : Optional.empty();
        }

        private String getInvestmentsByLenderNameAsString() {
            StringBuilder stringBuilder = new StringBuilder();
            investmentsByLenderName.forEach((name, investmentList) -> stringBuilder.append(System.lineSeparator()).append(
                    String.format("\t\tLender: %s (Investment Total: %.2f)", name, getLenderTotalInvestment(name))));
            return stringBuilder.toString();
        }

        private String getPendingDataAsString() {
            return String.format("\t\tTotal Investment: %.2f (Remaining: %.2f)",
                    getTotalInvestment(), getRemainingInvestment());
        }

        private String getActiveStatusTimeAsString() {
            return String.format("\t\tActive Since: %s %d",
                    engine.getTimeManager().TIME_UNIT_NAME, statusTimes.get(Status.ACTIVE));
        }

        private String getNextInstallmentDataAsString() {
            Optional<Integer> nextInstallmentTime = getNextInstallmentTime();
            String nextInstallmentTimeAsString = nextInstallmentTime.isPresent() ? String.format("%s %d", engine.getTimeManager().TIME_UNIT_NAME, nextInstallmentTime.get()) : "N/A";
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
                    engine.getTimeManager().TIME_UNIT_NAME, statusTimes.get(Status.ACTIVE),
                    engine.getTimeManager().TIME_UNIT_NAME, statusTimes.get(Status.FINISHED));
        }

        private String getStringHeader() {

            return String.format(
                    "%s" + System.lineSeparator()
                            + "\tStatus: %s",
                    getOriginalRequestAsString(), status) + System.lineSeparator() + "\tADDITIONAL INFORMATION:";
        }

        public String getOriginalRequestAsString() {
            return String.format(
                    "LOAN DETAILS:" + System.lineSeparator()
                            + "\tID: %s" + System.lineSeparator()
                            + "\tBorrower: %s" + System.lineSeparator()
                            + "\tCategory: %s" + System.lineSeparator()
                            + "\tCapital: %.2f (%.2f every %d %s for %d %s)" + System.lineSeparator()
                            + "\tInterest: %.2f%% (Total: %.2f | %.2f every %d %s)" + System.lineSeparator()
                            + "\tTotal: %.2f",
                    getID(), getOriginalRequest().getBorrowerName(), getOriginalRequest().getCategory(),
                    getOriginalRequest().getCapital(), getOriginalRequest().getPrincipalPerInstallment(),
                    getOriginalRequest().getInstallmentPeriod(), TimeManager.TIME_UNIT_NAME,
                    getOriginalRequest().getTerm(), TimeManager.TIME_UNIT_NAME, getOriginalRequest().getInterestRate() * 100,
                    getOriginalRequest().getTotalInterest(), getOriginalRequest().getInterestPerInstallment(),
                    getOriginalRequest().getInstallmentPeriod(), TimeManager.TIME_UNIT_NAME, getOriginalRequest().getTotal());
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
        public LoanDetails toDTO() {
            Map<LoanStatusData, Integer> statusTimesDTO = new HashMap<>();
            statusTimes.forEach((status, time) -> statusTimesDTO.put(Status.toDTO(status), time));
            Map<String, List<InvestmentDetails>> investmentsByLenderNameDTO = new HashMap<>();
            investmentsByLenderName.forEach((lenderName, investmentList) -> investmentsByLenderNameDTO.put(lenderName, investmentList.stream().map(Investment::toDTO).collect(Collectors.toList())));
            return new LoanDetails(this.toShortString(), this.toString(), getID(), getOriginalRequest().getBorrowerName(), getOriginalRequest().getCategory(),
                    Status.toDTO(status), statusTimesDTO, getTotalInvestment(), investmentsByLenderNameDTO, originalRequest.getTerm(),
                    getPassedTerm(), getOriginalRequest().getInstallmentPeriod(), delayedInstallmentCount, getPreviousInstallmentTime(),
                    engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName()).getActiveLoanCount(),
                    getNextInstallmentTime(), getOriginalRequest().getInterestPerInstallment(), getOriginalRequest().getCapital(),
                    paidInterest, paidPrincipal, getAccount().toDTO(), paymentNotifications);
        }

        @Override
        public String toString() {

            StringBuilder stringBuilder = new StringBuilder(getStringHeader());

            switch (status) {
                case PENDING:
                    stringBuilder.append(getInvestmentsByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPendingDataAsString());
                    break;
                case ACTIVE:
                    stringBuilder.append(getInvestmentsByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getActiveStatusTimeAsString());
                    stringBuilder.append(System.lineSeparator()).append(getNextInstallmentDataAsString());
                    stringBuilder.append(System.lineSeparator()).append(getLedgerAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPrincipalAsString());
                    stringBuilder.append(System.lineSeparator()).append(getInterestAsString());
                    break;
                case RISK:
                    stringBuilder.append(getInvestmentsByLenderNameAsString());
                    stringBuilder.append(System.lineSeparator()).append(getActiveStatusTimeAsString());
                    stringBuilder.append(System.lineSeparator()).append(getNextInstallmentDataAsString());
                    stringBuilder.append(System.lineSeparator()).append(getLedgerAsString());
                    stringBuilder.append(System.lineSeparator()).append(getPrincipalAsString());
                    stringBuilder.append(System.lineSeparator()).append(getInterestAsString());
                    stringBuilder.append(System.lineSeparator()).append(getDelayedInstallmentsAsString());
                    break;
                case FINISHED:
                    stringBuilder.append(getInvestmentsByLenderNameAsString());
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
        public void addInvestment(Investment investment) {
            if (status == Status.PENDING) {
                String lenderName = investment.getOriginalRequest().getLenderName();
                List<Investment> investmentList = investmentsByLenderName.get(lenderName);
                if (investmentList != null) {
                    investmentList.add(investment);
                }
                else {
                    investmentList = new ArrayList<>();
                    investmentList.add(investment);
                    investmentsByLenderName.put(lenderName, investmentList);
                }

                engine.getCustomerManager().getCustomersByName().get(investment.getOriginalRequest().getLenderName()).getAccount()
                        .executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), investment.getInvestmentTotal(), 0, engine.getTimeManager().getCurrentTime());
                if (getTotalInvestment() >= originalRequest.getCapital()) {
                    setStatus(Status.ACTIVE);
                    getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName()).getAccount(), originalRequest.getCapital(), 0, engine.getTimeManager().getCurrentTime());
                }
            }
        }

        @Override
        public void executeAccumulatedDebtPayment() {
            BilateralTransactionRecord record = engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), getAccumulatedDebtPrincipal(), getAccumulatedDebtInterest(), engine.getTimeManager().getCurrentTime());
            if (record.getStatus() == TransactionStatusData.SUCCESSFUL) {
                paidPrincipal += getAccumulatedDebtPrincipal();
                paidInterest += getAccumulatedDebtInterest();
                if (getRemainingTotal() == 0) {
                    setStatus(Status.FINISHED);
                    investmentsByLenderName.keySet().forEach(name -> {
                        double lenderTotalInvestment = getLenderTotalInvestment(name);
                        getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                                engine.getCustomerManager().getCustomersByName().get(name).getAccount(), lenderTotalInvestment, getInvestmentInterest(lenderTotalInvestment), engine.getTimeManager().getCurrentTime());
                    });
                }
                else setStatus(Status.ACTIVE);
            }
        }

        @Override
        public void executeRemainingTotalPayment() {
            BilateralTransactionRecord record = engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), getRemainingPrincipal(), getRemainingInterest(), engine.getTimeManager().getCurrentTime());
            if (record.getStatus() == TransactionStatusData.SUCCESSFUL) {
                paidPrincipal += getRemainingPrincipal();
                paidInterest += getRemainingInterest();
                setStatus(Status.FINISHED);
                investmentsByLenderName.keySet().forEach(name -> {
                    double lenderTotalInvestment = getLenderTotalInvestment(name);
                    getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                            engine.getCustomerManager().getCustomersByName().get(name).getAccount(), lenderTotalInvestment, getInvestmentInterest(lenderTotalInvestment), engine.getTimeManager().getCurrentTime());
                });
            }
        }

        @Override
        public void executeRiskPayment(double paymentTotal) {
            double paymentInterest = paymentTotal * originalRequest.getInterestRate();
            double paymentPrincipal = paymentTotal - paymentInterest;
            BilateralTransactionRecord record = engine.getCustomerManager().getCustomersByName().get(originalRequest.getBorrowerName())
                    .getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER, getAccount(), paymentPrincipal, paymentInterest, engine.getTimeManager().getCurrentTime());
            if (record.getStatus() == TransactionStatusData.SUCCESSFUL) {
                paidPrincipal += paymentPrincipal;
                paidInterest += paymentInterest;
                if (getRemainingTotal() == 0) {
                    setStatus(Status.FINISHED);
                    investmentsByLenderName.keySet().forEach(name -> {
                        double lenderTotalInvestment = getLenderTotalInvestment(name);
                        getAccount().executeTransaction(BilateralTransaction.Type.TRANSFER,
                                engine.getCustomerManager().getCustomersByName().get(name).getAccount(), lenderTotalInvestment, getInvestmentInterest(lenderTotalInvestment), engine.getTimeManager().getCurrentTime());
                    });
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
                statusTimes.put(status, engine.getTimeManager().getCurrentTime());
            if (status == Status.RISK) {
                investmentsByLenderName.values().forEach(investmentList -> investmentList.forEach(investment -> investment.setForSale(false)));
            }
            addLoanStatusChangeNotification(new LoanStatusChangeNotification(getID(), Status.toDTO(previousStatus), Status.toDTO(status)));
            Arrays.stream(eventListeners.getListeners(LoanStatusUpdateListener.class)).forEach(listener -> listener.statusUpdated(new LoanStatusUpdateEvent(Status.toDTO(previousStatus), toDTO())));
        }

        @Override
        public void incrementDelayedInstallmentCount() {
            delayedInstallmentCount++;
        }

        @Override
        public boolean isInstallmentTime() {
            return (status == Status.ACTIVE || status == Status.RISK) && getPassedTerm() != 0 && getPassedTerm() % originalRequest.getInstallmentPeriod() == 0;
        }
    }

    public static class BasicInvestment implements Investment {

        private final InvestmentRequest originalRequest;

        private final String loanID;

        private final double investmentTotal;

        private boolean forSale = false;

        private BasicInvestment(InvestmentRequest request, String loanID, double investmentTotal) {
            this.originalRequest = request;
            this.loanID = loanID;
            this.investmentTotal = investmentTotal;
        }

        @Override
        public void setForSale(boolean forSale) {
            this.forSale = forSale;
        }

        @Override
        public boolean getForSale() {
            return forSale;
        }

        @Override
        public InvestmentRequest getOriginalRequest() {
            return originalRequest;
        }

        @Override
        public String getLoanID() {
            return loanID;
        }

        @Override
        public double getInvestmentTotal() {
            return investmentTotal;
        }

        @Override
        public InvestmentDetails toDTO() {
            return new InvestmentDetails(originalRequest.getLenderName(), loanID, investmentTotal, forSale);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BasicInvestment that = (BasicInvestment) o;
            return Double.compare(that.investmentTotal, investmentTotal) == 0 && forSale == that.forSale && Objects.equals(originalRequest, that.originalRequest) && Objects.equals(loanID, that.loanID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(originalRequest, loanID, investmentTotal, forSale);
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
