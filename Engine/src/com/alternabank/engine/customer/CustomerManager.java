package com.alternabank.engine.customer;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.customer.LoanStatusChangeNotificationsAndVersion;
import com.alternabank.dto.customer.PaymentNotificationsAndVersion;
import com.alternabank.dto.loan.notification.LoanStatusChangeNotification;
import com.alternabank.dto.loan.notification.PaymentNotification;
import com.alternabank.dto.loan.request.InvestmentRequest;
import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.engine.account.AbstractOwnedAccount;
import com.alternabank.dto.customer.CustomerBalanceDetails;
import com.alternabank.dto.customer.CustomerDetails;
import com.alternabank.engine.customer.state.CustomerManagerState;
import com.alternabank.engine.loan.Loan;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.engine.transaction.UnilateralTransaction;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.engine.Engine;

import javax.swing.event.EventListenerList;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerManager {

    private final Engine engine;
    private final Map<String, Customer> customersByName = new HashMap<>();
    private final EventListenerList eventListeners = new EventListenerList();

    private final List<Map<String, CustomerDetails>> previousCustomerDetailsStates = new ArrayList<>();

    public CustomerManager(Engine engine) {
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public CustomerManagerState createCustomerManagerState() {
        return new CustomerManagerState(customersByName);
    }

    public void saveCustomerDetails() {
        int currentTime = engine.getTimeManager().getCurrentTime();
        if (previousCustomerDetailsStates.size() == currentTime)
            previousCustomerDetailsStates.add(getCustomerDetails());
        else previousCustomerDetailsStates.set(currentTime, getCustomerDetails());
    }

    public void restoreCustomerManager(CustomerManagerState customerManagerState) {
        this.customersByName.clear();
        this.customersByName.putAll(customerManagerState.getCustomersByName());
    }

    public Map<String, Customer> getCustomersByName() {
        return customersByName;
    }

    public Set<CustomerBalanceDetails> getCustomerBalanceDetails() {
        return customersByName.values().stream().map(Customer::getCustomerBalanceDetails).collect(Collectors.toSet());
    }

    public boolean customerExists(String name) {
        return customersByName.containsKey(name);
    }

    public Customer createCustomer(String name) {
        Customer customer = null;

        if(!customerExists(name)) {
            customer = new Customer(name);
            customersByName.put(name, customer);
        }

        return customer;
    }

    public boolean removeCustomer(String name) {
        boolean success = false;

        if(engine.getLoanManager().getLoansByID().values().stream().noneMatch(loan ->
                loan.getOriginalRequest().getBorrowerName().equals(name) ||
                        loan.getInvestmentsByLenderName().containsKey(name))) {
            customersByName.remove(name);
            success = true;
        }

        return success;
    }

    public void reset() {
        customersByName.clear();
    }

    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        eventListeners.add(UnilateralTransactionListener.class, listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        eventListeners.add(BilateralTransactionListener.class, listener);
    }

    public Set<String> getCustomerNames() {
        return customersByName.keySet();
    }

    public Map<String, CustomerDetails> getCustomerDetails() {
        return customersByName.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDTO()));
    }

    public Map<String, CustomerDetails> getCustomerDetails(int time) {
        return previousCustomerDetailsStates.get(time);
    }

    public CustomerDetails getCustomerDetailsByName(String name) {
        return customersByName.get(name).toDTO();
    }

    public void depositFunds(String customerName, double total) {
        if(customerExists(customerName)) {
            CustomerManager.Customer customer = customersByName.get(customerName);
            customer.getAccount().executeTransaction(UnilateralTransaction.Type.DEPOSIT, total, engine.getTimeManager().getCurrentTime());
        }
    }

    public void withdrawFunds(String customerName, double total) {
        if(customerExists(customerName)) {
            CustomerManager.Customer customer = customersByName.get(customerName);
            customer.getAccount().executeTransaction(UnilateralTransaction.Type.WITHDRAWAL, total, engine.getTimeManager().getCurrentTime());
        }
    }

    public boolean postInvestmentRequest(InvestmentRequest investmentRequest) {
        return customersByName.get((investmentRequest.getLenderName())).postInvestmentRequest(investmentRequest);
    }

    public class Customer extends AbstractOwnedAccount.Owner implements Lender, Borrower {

        private final List<PaymentNotification> paymentNotifications = new ArrayList<>();

        private final List<LoanStatusChangeNotification> loanStatusChangeNotifications = new ArrayList<>();

        private final Set<String> postedLoanIDs = new HashSet<>();
        private final Set<String> investedLoanIDs = new HashSet<>();

        private Customer(String name) {
            new CustomerAccount(name).super(name);
        }

        @Override
        public boolean postedLoan(String loanID) {
            return postedLoanIDs.contains(loanID);
        }

        public LoanDetails getLoanDetails(String loanID) {
            LoanDetails loanDetails;
            loanDetails = postedLoanIDs.stream().filter(id -> id.equals(loanID)).map(id -> engine.getLoanManager().getLoan(id).toDTO()).findFirst().orElse(null);
            if (loanDetails == null)
                loanDetails = investedLoanIDs.stream().filter(id -> id.equals(loanID)).map(id -> engine.getLoanManager().getLoan(id).toDTO()).findFirst().orElse(null);
            return loanDetails;
        }

        public void addPaymentNotification(PaymentNotification paymentNotification) {
            paymentNotifications.add(paymentNotification);
        }

        public void addLoanStatusChangeNotification(LoanStatusChangeNotification loanStatusChangeNotification) {
            loanStatusChangeNotifications.add(loanStatusChangeNotification);
        }

        public List<PaymentNotification> getPaymentNotifications() {
            return Collections.unmodifiableList(paymentNotifications);
        }

        public List<LoanStatusChangeNotification> getLoanStatusChangeNotifications() {
            return Collections.unmodifiableList(loanStatusChangeNotifications);
        }

        public List<PaymentNotification> getPaymentNotifications(int fromIndex) {
            if (fromIndex < 0 || fromIndex > paymentNotifications.size())
                fromIndex = 0;
            return paymentNotifications.subList(fromIndex, paymentNotifications.size());
        }

        public List<LoanStatusChangeNotification> getLoanStatusChangeNotifications(int fromIndex) {
            if (fromIndex < 0 || fromIndex > loanStatusChangeNotifications.size())
                fromIndex = 0;
            return loanStatusChangeNotifications.subList(fromIndex, loanStatusChangeNotifications.size());
        }

        public int getPaymentNotificationsVersion() {
            return paymentNotifications.size();
        }

        public int getLoanStatusChangeNotificationsVersion() {
            return loanStatusChangeNotifications.size();
        }

        public CustomerBalanceDetails getCustomerBalanceDetails() {
            return new CustomerBalanceDetails(getName(), getAccount().getBalance());
        }

        @Override
        public boolean postLoanRequest(LoanRequest loanRequest) {
            boolean success;
            Loan loan = engine.getLoanManager().createLoan(loanRequest);

            success = loan != null;

            if(success) {
                postedLoanIDs.add(loanRequest.getID());
            }

            return success;
        }

        @Override
        public Loan getPostedLoan(String loanID) {
            return postedLoan(loanID) ? engine.getLoanManager().getLoan(loanID) : null;
        }

        @Override
        public int getActiveLoanCount() {
            return Math.toIntExact(postedLoanIDs.stream()
                    .filter(loanId -> {
                        Loan loan = engine.getLoanManager().getLoan(loanId);
                        return loan.getStatus() == Loan.Status.ACTIVE || loan.getStatus() == Loan.Status.RISK;
                    }).count());
        }

        @Override
        public Set<String> getPostedLoanIDs() {
            return postedLoanIDs;
        }

        public Set<LoanDetails> postedLoansToDTO() {
            return postedLoanIDs.stream().map(loanID -> engine.getLoanManager().getLoan(loanID).toDTO()).collect(Collectors.toSet());
        }

        public Set<LoanDetails> investedLoansToDTO() {
            return investedLoanIDs.stream().map(loanID -> engine.getLoanManager().getLoan(loanID).toDTO()).collect(Collectors.toSet());
        }

        @Override
        public boolean postInvestmentRequest(InvestmentRequest investmentRequest) {
            boolean success = engine.getLoanManager().executeInvestmentRequest(investmentRequest);

            if(success) {
                investedLoanIDs.addAll(investmentRequest.getChosenLoanIDs());
            }

            return success;
        }

        @Override
        public boolean postRemainingLoanPortionForSale(String loanID) {
            boolean success = true;
            if (investedLoanIDs.contains(loanID)) {
                engine.getLoanManager().postRemainingInvestmentForSale(getName(), loanID);
            }
            else success = false;

            return success;
        }

        @Override
        public boolean buyRemainingLoanPortion(String loanID, String sellerName) {
            return engine.getLoanManager().executeRemainingInvestmentSale(loanID, getName(), sellerName);
        }

        @Override
        public Set<String> getInvestedLoanIDs() {
            return investedLoanIDs;
        }

        public CustomerDetails toDTO() {
            return new CustomerDetails(toString(), getName(), getAccount().toDTO(), postedLoansToDTO(), investedLoansToDTO(), new PaymentNotificationsAndVersion(paymentNotifications, getPaymentNotificationsVersion()), new LoanStatusChangeNotificationsAndVersion(loanStatusChangeNotifications, getLoanStatusChangeNotificationsVersion()));
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(String.format("CUSTOMER DETAILS:" + System.lineSeparator()
                            +"\tCustomer Name: %s" + System.lineSeparator()
                            +"\t%s",
                    getName(), getAccount().toString().replace(System.lineSeparator(), System.lineSeparator() + "\t")));

            if(!postedLoanIDs.isEmpty()) {
                stringBuilder.append(System.lineSeparator()).append("\tPOSTED LOANS:");
                postedLoanIDs.forEach(loanID -> stringBuilder.append(System.lineSeparator()).append("\t\t").append(engine.getLoanManager().getLoan(loanID).toShortString().replace(System.lineSeparator(), System.lineSeparator() + "\t\t")));
            }

            if(!investedLoanIDs.isEmpty()) {
                stringBuilder.append(System.lineSeparator()).append("\tINVESTED LOANS:");
                investedLoanIDs.forEach(loanID -> stringBuilder.append(System.lineSeparator()).append("\t\t").append(engine.getLoanManager().getLoan(loanID).toShortString().replace(System.lineSeparator(), System.lineSeparator() + "\t\t")));
            }

            return stringBuilder.toString();
        }
    }

    public class CustomerAccount extends AbstractOwnedAccount{

        private CustomerAccount(String ownerName) {
            super(ownerName, ownerName);
        }

        @Override
        public EventListenerList getEventListeners() {
            return eventListeners;
        }
    }
}
