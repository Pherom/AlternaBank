package com.alternabank.console.ui.form.loan;

import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.request.InvestmentRequestBuilder;
import com.alternabank.console.ui.form.AbstractMultipleChoiceForm;

import java.util.Set;
import java.util.stream.Collectors;

public class FilteredLoanChoiceForm extends AbstractMultipleChoiceForm<LoanDetails> {
    protected FilteredLoanChoiceForm(Set<LoanDetails> loanDetails, InvestmentRequestBuilder investmentRequestBuilder) {
        super("Choose loans to invest in:",
                loanDetails.stream()
                        .filter(details -> !details.getBorrowerName().equals(investmentRequestBuilder.getLenderName()))
                        .filter(details -> details.getStatus() == Loan.Status.PENDING)
                        .filter(details -> investmentRequestBuilder.getCategoriesOfInterest().contains(details.getCategory()))
                        .filter(details -> investmentRequestBuilder.getMinimumInterestRate() == Investment.DEFAULT_VALUE ||
                                details.getInterestRate() >= investmentRequestBuilder.getMinimumInterestRate())
                        .filter(details -> investmentRequestBuilder.getMinimumInterestPerTimeUnit() == Investment.DEFAULT_VALUE ||
                                details.getInterestPerTimeUnit() >= investmentRequestBuilder.getMinimumInterestPerTimeUnit())
                        .filter(details -> investmentRequestBuilder.getMinimumLoanTerm() == Investment.DEFAULT_VALUE ||
                                details.getOriginalTerm() >= investmentRequestBuilder.getMinimumLoanTerm()).collect(Collectors.toSet())
            );
    }

    @Override
    public void displayUserSelection(LoanDetails selection) {
        System.out.println(selection.getId());
    }
}
