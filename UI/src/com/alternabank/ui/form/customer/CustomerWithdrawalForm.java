package com.alternabank.ui.form.customer;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.ui.form.AbstractForm;
import com.alternabank.ui.form.Form;
import com.alternabank.ui.form.SingleChoiceForm;

import java.util.Set;

public class CustomerWithdrawalForm extends AbstractForm<CustomerWithdrawalForm.CustomerWithdrawalRequest> {

    private final SingleChoiceForm<CustomerBalanceDetails> customerChoiceByBalanceForm;
    private final Form<Double> withdrawalTotalForm;

    public CustomerWithdrawalForm(Set<CustomerBalanceDetails> customerBalanceDetails) {
        super("Customer account withdrawal request:");
        this.customerChoiceByBalanceForm = new CustomerChoiceByBalanceForm(customerBalanceDetails);
        this.withdrawalTotalForm = new CustomerWithdrawalForm.WithdrawalTotalForm();
    }

    @Override
    protected boolean validateUserResponse() {
        return true;
    }

    @Override
    public CustomerWithdrawalForm.CustomerWithdrawalRequest getResults() {
        return new CustomerWithdrawalForm.CustomerWithdrawalRequest(customerChoiceByBalanceForm.getResults().getName(), withdrawalTotalForm.getResults());
    }

    @Override
    public void display() {
        customerChoiceByBalanceForm.display();
        withdrawalTotalForm.display();
    }

    public static class CustomerWithdrawalRequest {

        private final String customerName;
        private final double total;

        private CustomerWithdrawalRequest(String customerName, double total) {
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

    public static class WithdrawalTotalForm extends AbstractForm<Double> {

        private WithdrawalTotalForm() {
            super("How much would you like to withdraw?:");
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
