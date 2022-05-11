package com.alternabank.console.ui.form.customer;

import com.alternabank.console.ui.form.AbstractSingleChoiceForm;

import java.util.Set;

public class CustomerChoiceForm extends AbstractSingleChoiceForm<String> {

    CustomerChoiceForm(Set<String> customerNames) {
        super("Choose customer account:", customerNames);
    }

}
