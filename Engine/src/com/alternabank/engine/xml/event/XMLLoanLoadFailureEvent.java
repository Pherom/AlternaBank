package com.alternabank.engine.xml.event;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.xml.XMLLoader;
import com.alternabank.engine.xml.generated.AbsLoan;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class XMLLoanLoadFailureEvent extends XMLLoadFailureEvent<AbsLoan> {

    public XMLLoanLoadFailureEvent(XMLLoader source, List<XMLLoadFailureEvent.Cause<AbsLoan>> causes, AbsLoan trigger) {
        super(source, causes, trigger);
    }

    public enum Cause implements XMLLoadFailureEvent.Cause<AbsLoan> {

        EMPTY_LOAN_ID((absLoan) -> absLoan.getId().isEmpty(),
                (absLoan) -> "Found loan with empty ID!"),
        LOAN_ID_COLLISION((absLoan) -> LoanManager.getInstance().loanExists(absLoan.getId()),
                (absLoan) -> String.format("Found multiple loans with identical ID \"%s\"!", absLoan.getId())),
        NO_MATCHING_LOAN_CATEGORY((absLoan) -> !LoanManager.getInstance().getAvailableCategories().contains(absLoan.getAbsCategory()),
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%s\" set as category but no matching loan category found!", absLoan.getId(), absLoan.getAbsCategory())),
        NO_MATCHING_BORROWER((absLoan) -> !CustomerManager.getInstance().customerExists(absLoan.getAbsOwner()),
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%s\" set as owner but no matching customer found!", absLoan.getId(), absLoan.getAbsOwner())),
        LOAN_CAPITAL_TOO_LOW((absLoan) -> absLoan.getAbsCapital() <= Loan.CAPITAL_LOWER_BOUND,
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%.2f\" set as capital which is lower than the lower bound (%.2f)!", absLoan.getId(), (double)absLoan.getAbsCapital(), Loan.CAPITAL_LOWER_BOUND)),
        LOAN_INTEREST_TOO_LOW((absLoan) -> absLoan.getAbsIntristPerPayment() < Loan.MINIMUM_INTEREST,
                (absLoan) -> String.format("Loan with id: \"%s\" has an interest rate of %.2f%% which is lower than the minimum (%.2f)!", absLoan.getId(), (absLoan.getAbsIntristPerPayment() / (double)(absLoan.getAbsCapital() / (absLoan.getAbsTotalYazTime() / absLoan.getAbsPaysEveryYaz()))) * 100, Loan.MINIMUM_INTEREST)),
        LOAN_INTEREST_TOO_HIGH((absLoan) -> absLoan.getAbsIntristPerPayment() > Loan.MAXIMUM_INTEREST,
                (absLoan) -> String.format("Loan with id: \"%s\" has an interest rate of %.2f%% which is higher than the maximum (%.2f)!", absLoan.getId(), (absLoan.getAbsIntristPerPayment() / (double)(absLoan.getAbsCapital() / (absLoan.getAbsTotalYazTime() / absLoan.getAbsPaysEveryYaz()))) * 100, Loan.MAXIMUM_INTEREST)),
        LOAN_TERM_TOO_LOW((absLoan) -> absLoan.getAbsTotalYazTime() < Loan.MINIMUM_TERM,
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as loan term which is lower than the minimum (%d)!", absLoan.getId(), absLoan.getAbsTotalYazTime(), Loan.MINIMUM_TERM)),
        LOAN_INSTALLMENT_PERIOD_TOO_LOW((absLoan) -> absLoan.getAbsPaysEveryYaz() < Loan.MINIMUM_INSTALLMENT_PERIOD,
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as installment period which is lower than the minimum (%d)!", absLoan.getId(), absLoan.getAbsTotalYazTime(), Loan.MINIMUM_INSTALLMENT_PERIOD)),
        LOAN_TERM_NOT_DIVISIBLE_BY_INSTALLMENT_PERIOD((absLoan) -> absLoan.getAbsTotalYazTime() % absLoan.getAbsPaysEveryYaz() != 0,
                (absLoan) -> String.format("Loan with id: \"%s\" has \"%d\" set as loan term which is not divisible by the set installment period \"%d\"!", absLoan.getId(), absLoan.getAbsTotalYazTime(), absLoan.getAbsPaysEveryYaz()));

        private final Predicate<AbsLoan> predicate;
        private final Function<AbsLoan, String> errorMessageGenerator;

        Cause(Predicate<AbsLoan> predicate, Function<AbsLoan, String> errorMessageGenerator) {
            this.predicate = predicate;
            this.errorMessageGenerator = errorMessageGenerator;
        }

        @Override
        public Predicate<AbsLoan> getPredicate() {
            return predicate;
        }

        @Override
        public String getErrorMessage(AbsLoan trigger) {
            return errorMessageGenerator.apply(trigger);
        }
    }

}
