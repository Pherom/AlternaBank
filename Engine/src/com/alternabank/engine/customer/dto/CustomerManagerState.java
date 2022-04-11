package com.alternabank.engine.customer.dto;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;

import java.util.*;

public class CustomerManagerState {

    private final Map<String, CustomerManager.Customer> customersByName = new HashMap<>();
    private final List<UnilateralTransactionListener> unilateralTransactionListeners = new LinkedList<>();
    private final List<BilateralTransactionListener> bilateralTransactionListeners = new LinkedList<>();

    public CustomerManagerState(Map<String, CustomerManager.Customer> customersByName,
                                List<UnilateralTransactionListener> unilateralTransactionListeners,
                                List<BilateralTransactionListener> bilateralTransactionListeners) {
        this.customersByName.putAll(customersByName);
        this.unilateralTransactionListeners.addAll(unilateralTransactionListeners);
        this.bilateralTransactionListeners.addAll(bilateralTransactionListeners);
    }

    public Map<String, CustomerManager.Customer> getCustomersByName() {
        return customersByName;
    }

    public List<UnilateralTransactionListener> getUnilateralTransactionListeners() {
        return unilateralTransactionListeners;
    }

    public List<BilateralTransactionListener> getBilateralTransactionListeners() {
        return bilateralTransactionListeners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerManagerState that = (CustomerManagerState) o;
        return Objects.equals(customersByName, that.customersByName) && Objects.equals(unilateralTransactionListeners, that.unilateralTransactionListeners) && Objects.equals(bilateralTransactionListeners, that.bilateralTransactionListeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customersByName, unilateralTransactionListeners, bilateralTransactionListeners);
    }
}
