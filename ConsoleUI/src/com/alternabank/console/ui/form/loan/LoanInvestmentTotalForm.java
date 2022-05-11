package com.alternabank.console.ui.form.loan;

import com.alternabank.console.ui.form.AbstractForm;

public class LoanInvestmentTotalForm extends AbstractForm<Double> {

    private final double investmentLimit;

    protected LoanInvestmentTotalForm(double investmentLimit) {
        super("Enter investment total:");
        this.investmentLimit = investmentLimit;
    }

    @Override
    protected boolean validateUserResponse() {
        boolean valid = true;

        try {
            double investmentTotal = Double.parseDouble(getUserResponse());

            if(investmentTotal <= 0 || investmentTotal > investmentLimit)
                valid = false;

        } catch (NumberFormatException e) {
            valid = false;
        }

        return valid;
    }

    @Override
    public Double getResults() {
        return Double.parseDouble(getUserResponse());
    }

}
