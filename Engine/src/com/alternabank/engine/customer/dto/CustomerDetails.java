package com.alternabank.engine.customer.dto;

import com.alternabank.engine.account.dto.AccountDetails;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.user.UserManager;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerDetails {

    private final String customerAsString;
    private final String name;
    private final AccountDetails accountDetails;
    private final Set<LoanDetails> postedLoanDetails;
    private final Set<LoanDetails> investedLoanDetails;

    public CustomerDetails(CustomerManager.Customer customer) {
        customerAsString = customer.toString();
        name = customer.getName();
        accountDetails = customer.getAccount().toAccountDetails();
        postedLoanDetails = customer.getPostedLoansIDs().stream().map(loanID -> UserManager.getInstance().getAdmin().getLoanManager().getLoan(loanID).toLoanDetails()).collect(Collectors.toSet());
        investedLoanDetails = customer.getInvestedLoansIDs().stream().map(loanID -> UserManager.getInstance().getAdmin().getLoanManager().getLoan(loanID).toLoanDetails()).collect(Collectors.toSet());
    }

    public String getName() {
        return name;
    }

    public AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public Set<LoanDetails> getPostedLoanDetails() {
        return postedLoanDetails;
    }

    public Set<LoanDetails> getInvestedLoanDetails() {
        return investedLoanDetails;
    }

    public Set<LoanDetails> getPostedLoanDetailsByStatus(Loan.Status status) {
        return postedLoanDetails.stream().filter(loanDetails -> loanDetails.getStatus() == status).collect(Collectors.toSet());
    }

    public Set<LoanDetails> getInvestedLoanDetailsByStatus(Loan.Status status) {
        return investedLoanDetails.stream().filter(loanDetails -> loanDetails.getStatus() == status).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return customerAsString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDetails that = (CustomerDetails) o;
        return Objects.equals(customerAsString, that.customerAsString) && Objects.equals(name, that.name) && Objects.equals(accountDetails, that.accountDetails) && Objects.equals(postedLoanDetails, that.postedLoanDetails) && Objects.equals(investedLoanDetails, that.investedLoanDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerAsString, name, accountDetails, postedLoanDetails, investedLoanDetails);
    }
}
