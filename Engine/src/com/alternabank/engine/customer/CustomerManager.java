package com.alternabank.engine.customer;

import com.alternabank.engine.account.AbstractOwnedAccount;
import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.customer.dto.CustomerManagerState;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.loan.request.LoanRequest;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.util.*;
import java.util.stream.Collectors;

public class CustomerManager {

    private static CustomerManager instance = null;
    private final Map<String, Customer> customersByName;
    private final List<UnilateralTransactionListener> unilateralTransactionListeners = new LinkedList<>();
    private final List<BilateralTransactionListener> bilateralTransactionListeners = new LinkedList<>();

    private CustomerManager() {
        customersByName = new HashMap<>();
    }

    public static CustomerManager getInstance() {
        if(instance == null)
            instance = new CustomerManager();
        return instance;
    }

    public CustomerManagerState createCustomerManagerState() {
        return new CustomerManagerState(customersByName, unilateralTransactionListeners, bilateralTransactionListeners);
    }

    public void restoreCustomerManager(CustomerManagerState customerManagerState) {
        this.customersByName.clear();
        this.customersByName.putAll(customerManagerState.getCustomersByName());
        this.unilateralTransactionListeners.clear();
        this.unilateralTransactionListeners.addAll(customerManagerState.getUnilateralTransactionListeners());
        this.bilateralTransactionListeners.clear();
        this.bilateralTransactionListeners.addAll(customerManagerState.getBilateralTransactionListeners());
    }

    public Map<String, Customer> getCustomersByName() {
        return Collections.unmodifiableMap(customersByName);
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

        if(LoanManager.getInstance().getLoansByID().values().stream().noneMatch(loan ->
                loan.getOriginalRequest().getBorrowerName().equals(name) ||
                        loan.getInvestmentByLenderName().containsKey(name))) {
            customersByName.remove(name);
            success = true;
        }

        return success;
    }

    public void addUnilateralTransactionListener(UnilateralTransactionListener listener) {
        unilateralTransactionListeners.add(listener);
    }

    public void addBilateralTransactionListener(BilateralTransactionListener listener) {
        bilateralTransactionListeners.add(listener);
    }

    public List<UnilateralTransactionListener> getUnilateralTransactionListeners() {
        return Collections.unmodifiableList(unilateralTransactionListeners);
    }

    public List<BilateralTransactionListener> getBilateralTransactionListeners() {
        return Collections.unmodifiableList(bilateralTransactionListeners);
    }

    public class Customer extends AbstractOwnedAccount.Owner implements Lender, Borrower {

        private final Set<String> postedLoansIDs = new HashSet<>();
        private final Set<String> investedLoansIDs = new HashSet<>();

        private Customer(String name) {
            new CustomerAccount(name).super(name);
        }

        public CustomerBalanceDetails getCustomerBalanceDetails() {
            return new CustomerBalanceDetails(this);
        }

        @Override
        public boolean postLoanRequest(LoanRequest loanRequest) {
            boolean success;
            Loan loan = LoanManager.getInstance().createLoan(loanRequest);

            success = loan != null;

            if(success) {
                postedLoansIDs.add(loanRequest.getID());
            }

            return success;
        }

        @Override
        public Set<String> getPostedLoansIDs() {
            return Collections.unmodifiableSet(postedLoansIDs);
        }

        @Override
        public boolean postInvestmentRequest(Investment.Request investmentRequest) {
            boolean success;
            Investment investment = LoanManager.getInstance().createInvestment(investmentRequest);

            success = investment != null;

            if(success) {
                investedLoansIDs.addAll(investment.getOriginalRequest().getChosenLoanIDs());
            }

            return success;
        }

        @Override
        public Set<String> getInvestedLoansIDs() {
            return Collections.unmodifiableSet(investedLoansIDs);
        }

        public CustomerDetails toCustomerDetails() {
            return new CustomerDetails(this);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(String.format("CUSTOMER DETAILS:" + System.lineSeparator()
                            +"\tCustomer Name: %s" + System.lineSeparator()
                            +"\t%s",
                    getName(), getAccount().toString().replace(System.lineSeparator(), System.lineSeparator() + "\t")));

            if(!postedLoansIDs.isEmpty()) {
                stringBuilder.append(System.lineSeparator()).append("\tPOSTED LOANS:");
                postedLoansIDs.forEach(loanID -> stringBuilder.append(System.lineSeparator()).append("\t\t").append(LoanManager.getInstance().getLoan(loanID).toShortString().replace(System.lineSeparator(), System.lineSeparator() + "\t\t")));
            }

            if(!investedLoansIDs.isEmpty()) {
                stringBuilder.append(System.lineSeparator()).append("\tINVESTED LOANS:");
                investedLoansIDs.forEach(loanID -> stringBuilder.append(System.lineSeparator()).append("\t\t").append(LoanManager.getInstance().getLoan(loanID).toShortString().replace(System.lineSeparator(), System.lineSeparator() + "\t\t")));
            }

            return stringBuilder.toString();
        }
    }

    public class CustomerAccount extends AbstractOwnedAccount{

        private CustomerAccount(String ownerName) {
            super(ownerName, ownerName);
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