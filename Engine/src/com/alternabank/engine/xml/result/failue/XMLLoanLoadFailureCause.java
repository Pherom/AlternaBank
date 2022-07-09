package com.alternabank.engine.xml.result.failue;

import com.alternabank.engine.Engine;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.xml.generated.AbsLoan;

import java.util.function.BiPredicate;
import java.util.function.Function;

public enum XMLLoanLoadFailureCause implements XMLLoadFailureCause<AbsLoan, Engine> {

    EMPTY_LOAN_ID((absLoan, engine) -> absLoan.getId().isEmpty(),
            (absLoan) -> "Found loan with empty ID!"),
    LOAN_ID_COLLISION((absLoan, engine) -> engine.getLoanManager().loanExists(absLoan.getId()),
            (absLoan) -> String.format("Found multiple loans with identical ID \"%s\"!", absLoan.getId())),
    NO_MATCHING_LOAN_CATEGORY((absLoan, engine) -> !engine.getLoanManager().getAvailableCategories().contains(absLoan.getAbsCategory()),
            (absLoan) -> String.format("Loan with id: \"%s\" has \"%s\" set as category but no matching loan category found!", absLoan.getId(), absLoan.getAbsCategory())),
    LOAN_CAPITAL_TOO_LOW((absLoan, engine) -> absLoan.getAbsCapital() <= Loan.CAPITAL_LOWER_BOUND,
            (absLoan) -> String.format("Loan with id: \"%s\" has \"%.2f\" set as capital which is lower than the lower bound (%.2f)!", absLoan.getId(), (double) absLoan.getAbsCapital(), Loan.CAPITAL_LOWER_BOUND)),
    LOAN_INTEREST_TOO_LOW((absLoan, engine) -> absLoan.getAbsIntristPerPayment() < Loan.MINIMUM_INTEREST,
            (absLoan) -> String.format("Loan with id: \"%s\" has an interest rate of %.2f%% which is lower than the minimum (%.2f)!", absLoan.getId(), (absLoan.getAbsIntristPerPayment() / (double) (absLoan.getAbsCapital() / (absLoan.getAbsTotalYazTime() / absLoan.getAbsPaysEveryYaz()))) * 100, Loan.MINIMUM_INTEREST)),
    LOAN_INTEREST_TOO_HIGH((absLoan, engine) -> absLoan.getAbsIntristPerPayment() > Loan.MAXIMUM_INTEREST,
            (absLoan) -> String.format("Loan with id: \"%s\" has an interest rate of %.2f%% which is higher than the maximum (%.2f)!", absLoan.getId(), (absLoan.getAbsIntristPerPayment() / (double) (absLoan.getAbsCapital() / (absLoan.getAbsTotalYazTime() / absLoan.getAbsPaysEveryYaz()))) * 100, Loan.MAXIMUM_INTEREST)),
    LOAN_TERM_TOO_LOW((absLoan, engine) -> absLoan.getAbsTotalYazTime() < Loan.MINIMUM_TERM,
            (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as loan term which is lower than the minimum (%d)!", absLoan.getId(), absLoan.getAbsTotalYazTime(), Loan.MINIMUM_TERM)),
    LOAN_INSTALLMENT_PERIOD_TOO_LOW((absLoan, engine) -> absLoan.getAbsPaysEveryYaz() < Loan.MINIMUM_INSTALLMENT_PERIOD,
            (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as installment period which is lower than the minimum (%d)!", absLoan.getId(), absLoan.getAbsTotalYazTime(), Loan.MINIMUM_INSTALLMENT_PERIOD)),
    LOAN_TERM_NOT_DIVISIBLE_BY_INSTALLMENT_PERIOD((absLoan, engine) -> absLoan.getAbsTotalYazTime() % absLoan.getAbsPaysEveryYaz() != 0,
            (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as loan term which is not divisible by the set installment period \"%d\"!", absLoan.getId(), absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz()));

    private final BiPredicate<AbsLoan, Engine> predicate;
    private final Function<AbsLoan, String> errorMessageGenerator;

    XMLLoanLoadFailureCause(BiPredicate<AbsLoan, Engine> predicate, Function<AbsLoan, String> errorMessageGenerator) {
        this.predicate = predicate;
        this.errorMessageGenerator = errorMessageGenerator;
    }

    @Override
    public BiPredicate<AbsLoan, Engine> getPredicate() {
        return predicate;
    }

    @Override
    public String getErrorMessage(AbsLoan trigger) {
        return errorMessageGenerator.apply(trigger);
    }
}
