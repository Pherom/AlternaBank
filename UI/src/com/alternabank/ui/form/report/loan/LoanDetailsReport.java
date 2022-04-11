package com.alternabank.ui.form.report.loan;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.ui.form.report.Report;

import java.util.Set;

public class LoanDetailsReport implements Report {

    private final Set<LoanDetails> loanDetails;

    public LoanDetailsReport(Set<LoanDetails> loanDetails) {
        this.loanDetails = loanDetails;
    }

    @Override
    public void display() {
        System.out.println("Loan details report:");
        loanDetails.forEach(details -> System.out.println(System.lineSeparator() + details));
        System.out.println();
    }
}
