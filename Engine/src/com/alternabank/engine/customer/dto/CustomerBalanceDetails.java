package com.alternabank.engine.customer.dto;

import com.alternabank.engine.customer.CustomerManager;

import java.util.Objects;

public class CustomerBalanceDetails {

    private final String name;
    private final double balance;

    public CustomerBalanceDetails(CustomerManager.Customer customer) {
        this.name = customer.getName();
        this.balance = customer.getAccount().getBalance();
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("Name: %s" + System.lineSeparator()
                            + "Balance: %.2f" + System.lineSeparator(),
        this.name, this.balance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerBalanceDetails that = (CustomerBalanceDetails) o;
        return Double.compare(that.balance, balance) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, balance);
    }
}
