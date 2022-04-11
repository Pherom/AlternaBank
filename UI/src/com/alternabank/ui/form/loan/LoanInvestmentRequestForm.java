package com.alternabank.ui.form.loan;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.request.InvestmentRequestBuilder;
import com.alternabank.ui.form.*;
import com.alternabank.ui.form.customer.CustomerChoiceByBalanceForm;

import java.util.Set;
import java.util.stream.Collectors;

public class LoanInvestmentRequestForm extends AbstractForm<Investment.Request> {

    private final Set<LoanDetails> loanDetails;
    private InvestmentRequestBuilder investmentRequestBuilder;

    private final SingleChoiceForm<CustomerBalanceDetails> customerChoiceByBalanceForm;
    private final MultipleChoiceForm<String> loanCategoryChoiceForm;
    private final OptionsForm<YesNoForm.Option> filterByMinimumInterestRateDecisionForm = new YesNoForm("Would you like to filter by minimum interest rate? (Y/N)");
    private final OptionsForm<YesNoForm.Option> filterByMinimumLoanTermDecisionForm = new YesNoForm("Would you like to filter by minimum loan term? (Y/N)");
    private final OptionsForm<YesNoForm.Option> confirmLoanAssignmentForm = new YesNoForm("Confirm loan assignment request? (Y/N)");
    private MultipleChoiceForm<LoanDetails> filteredLoanChoiceForm;

    public LoanInvestmentRequestForm(Set<CustomerBalanceDetails> customerBalanceDetails, Set<String> loanCategories, Set<LoanDetails> loanDetails) {
        super("Loan assignment form:");
        customerChoiceByBalanceForm = new CustomerChoiceByBalanceForm(customerBalanceDetails);
        loanCategoryChoiceForm = new LoanCategoryChoiceForm(loanCategories);
        this.loanDetails = loanDetails;
    }

    @Override
    protected boolean validateUserResponse() {
        return true;
    }

    @Override
    public void display() {
        customerChoiceByBalanceForm.display();
        Form<Double> loanInvestmentTotalForm = new LoanInvestmentTotalForm(customerChoiceByBalanceForm.getResults().getBalance());
        loanInvestmentTotalForm.display();
        investmentRequestBuilder = new InvestmentRequestBuilder(customerChoiceByBalanceForm.getResults().getName(), loanInvestmentTotalForm.getResults());
        loanCategoryChoiceForm.display();
        investmentRequestBuilder.setCategoriesOfInterest(loanCategoryChoiceForm.getResults());
        filterByMinimumInterestRateDecisionForm.display();
        if(filterByMinimumInterestRateDecisionForm.getResults() == YesNoForm.Option.YES) {
            Form<Double> minimumInterestRateForm = new MinimumInterestRateForm();
            minimumInterestRateForm.display();
            investmentRequestBuilder.setMinimumInterestRate(minimumInterestRateForm.getResults());
        }

        filterByMinimumLoanTermDecisionForm.display();
        if(filterByMinimumLoanTermDecisionForm.getResults() == YesNoForm.Option.YES) {
            Form<Integer> minimumLoanTermForm = new MinimumLoanTermForm();
            minimumLoanTermForm.display();
            investmentRequestBuilder.setMinimumLoanTerm(minimumLoanTermForm.getResults());
        }

        filteredLoanChoiceForm = new FilteredLoanChoiceForm(loanDetails, investmentRequestBuilder);
        filteredLoanChoiceForm.display();
        if(!filteredLoanChoiceForm.getResults().isEmpty()) {
            investmentRequestBuilder.addLoansToInvestIn(filteredLoanChoiceForm.getResults().stream().map(LoanDetails::getId).collect(Collectors.toSet()));
            confirmLoanAssignmentForm.display();
        }
    }

    @Override
    public Investment.Request getResults() {

        if(!filteredLoanChoiceForm.getResults().isEmpty() && confirmLoanAssignmentForm.getResults() == YesNoForm.Option.YES) {
            return investmentRequestBuilder.build();
        }

        else return null;
    }
}
