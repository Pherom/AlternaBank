package com.alternabank.ui.form.loan;

import com.alternabank.ui.form.AbstractForm;

public class MinimumLoanTermForm extends AbstractForm<Integer> {

    protected MinimumLoanTermForm() {
        super("Choose minimum loan term:");
    }

    @Override
    protected boolean validateUserResponse() {
        boolean valid = true;

        try {
            int parsed = Integer.parseInt(getUserResponse());

            if(parsed <= 0)
                valid = false;

        } catch (NumberFormatException e) {
            valid = false;
        }

        return valid;
    }

    @Override
    public Integer getResults() {
        return Integer.parseInt(getUserResponse());
    }
}
