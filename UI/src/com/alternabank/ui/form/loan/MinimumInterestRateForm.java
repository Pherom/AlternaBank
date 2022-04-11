package com.alternabank.ui.form.loan;

import com.alternabank.ui.form.AbstractForm;

public class MinimumInterestRateForm extends AbstractForm<Double> {

    protected MinimumInterestRateForm() {
        super("Choose minimum interest rate (%):");
    }

    @Override
    protected boolean validateUserResponse() {
        boolean valid = true;

        try {
            double parsed = Double.parseDouble(getUserResponse());

            if(parsed <= 0)
                valid = false;

        } catch (NumberFormatException e) {
            valid = false;
        }

        return valid;
    }

    @Override
    public Double getResults() {
        return Double.parseDouble(getUserResponse()) / 100;
    }
}
