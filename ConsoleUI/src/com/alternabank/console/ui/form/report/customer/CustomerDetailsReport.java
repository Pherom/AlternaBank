package com.alternabank.console.ui.form.report.customer;

import com.alternabank.console.ui.form.report.Report;
import com.alternabank.engine.customer.dto.CustomerDetails;

import java.util.Set;

public class CustomerDetailsReport implements Report {

    private final Set<CustomerDetails> customerDetails;

    public CustomerDetailsReport(Set<CustomerDetails> customerDetails) {
        this.customerDetails = customerDetails;
    }

    @Override
    public void display() {
        System.out.println("Customer details report:");
        customerDetails.forEach(details -> System.out.println(System.lineSeparator() + details));
        System.out.println();
    }
}
