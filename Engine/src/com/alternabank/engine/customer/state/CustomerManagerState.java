package com.alternabank.engine.customer.state;

import com.alternabank.dto.customer.CustomerDetails;
import com.alternabank.engine.customer.CustomerManager;

import java.util.*;

public class CustomerManagerState {

    private final Map<String, CustomerManager.Customer> customersByName = new HashMap<>();

    public CustomerManagerState(Map<String, CustomerManager.Customer> customersByName) {
        this.customersByName.putAll(customersByName);
    }

    public Map<String, CustomerManager.Customer> getCustomersByName() {
        return customersByName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerManagerState that = (CustomerManagerState) o;
        return Objects.equals(customersByName, that.customersByName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customersByName);
    }
}
