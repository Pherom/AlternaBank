package com.alternabank.ui.form.loan;

import com.alternabank.ui.form.AbstractMultipleChoiceForm;

import java.util.Set;

public class LoanCategoryChoiceForm extends AbstractMultipleChoiceForm<String> {
    protected LoanCategoryChoiceForm(Set<String> categories) {
        super("Choose categories:", categories);
    }

    @Override
    public void displayUserSelection(String selection) {
        System.out.println(selection);
    }
}
