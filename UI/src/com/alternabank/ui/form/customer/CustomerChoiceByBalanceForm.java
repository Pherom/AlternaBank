package com.alternabank.ui.form.customer;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.ui.form.AbstractSingleChoiceForm;

import java.util.Set;

public class CustomerChoiceByBalanceForm extends AbstractSingleChoiceForm<CustomerBalanceDetails> {

    public CustomerChoiceByBalanceForm(Set<CustomerBalanceDetails> choices) {
        super("Choose customer:", choices);
    }

}
