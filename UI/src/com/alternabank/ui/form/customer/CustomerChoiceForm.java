package com.alternabank.ui.form.customer;

import com.alternabank.ui.form.AbstractSingleChoiceForm;

import java.util.Set;

public class CustomerChoiceForm extends AbstractSingleChoiceForm<String> {

    CustomerChoiceForm(Set<String> customerNames) {
        super("Choose customer account:", customerNames);
    }

}
