package com.alternabank.console.ui.form.customer;

import com.alternabank.console.ui.form.AbstractForm;
import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.console.ui.form.Form;
import com.alternabank.console.ui.form.SingleChoiceForm;

import java.util.Set;

public class CustomerDepositForm extends AbstractForm<CustomerDepositForm.CustomerDepositRequest> {

    private final SingleChoiceForm<CustomerBalanceDetails> customerChoiceByBalanceForm;
    private final Form<Double> depositTotalForm;

    public CustomerDepositForm(Set<CustomerBalanceDetails> customerBalanceDetails) {
        super("Customer account deposit request:");
        this.customerChoiceByBalanceForm = new CustomerChoiceByBalanceForm(customerBalanceDetails);
        this.depositTotalForm = new DepositTotalForm();
    }

    @Override
    protected boolean validateUserResponse() {
        return true;
    }

    @Override
    public CustomerDepositRequest getResults() {
        return new CustomerDepositRequest(customerChoiceByBalanceForm.getResults().getName(), depositTotalForm.getResults());
    }

    @Override
    public void display() {
        customerChoiceByBalanceForm.display();
        depositTotalForm.display();
    }

    public static class CustomerDepositRequest {

        private final String customerName;
        private final double total;

        private CustomerDepositRequest(String customerName, double total) {
            this.customerName = customerName;
            this.total = total;
        }

        public String getCustomerName() {
            return customerName;
        }

        public double getTotal() {
            return total;
        }

    }

    public static class DepositTotalForm extends AbstractForm<Double> {

        private DepositTotalForm() {
            super("How much would you like to deposit?:");
        }

        @Override
        protected boolean validateUserResponse() {
            double total;

            try {
                total = Double.parseDouble(getUserResponse());
            } catch (NumberFormatException e) {
                return false;
            }

            return total > 0;
        }

        @Override
        public Double getResults() {
            return Double.parseDouble(getUserResponse());
        }
    }
}
