package com.alternabank.dto.customer;

import com.alternabank.dto.account.AccountDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import com.alternabank.dto.loan.LoanDetails;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerDetails {

    private final String customerAsString;
    private final String name;
    private final AccountDetails accountDetails;
    private final Set<LoanDetails> postedLoanDetails;
    private final Set<LoanDetails> investedLoanDetails;

    private final PaymentNotificationsAndVersion paymentNotificationsAndVersion;

    private final LoanStatusChangeNotificationsAndVersion loanStatusChangeNotificationsAndVersion;

    public CustomerDetails(String customerAsString, String name, AccountDetails accountDetails, Set<LoanDetails> postedLoanDetails, Set<LoanDetails> investedLoanDetails, PaymentNotificationsAndVersion paymentNotificationsAndVersion, LoanStatusChangeNotificationsAndVersion loanStatusChangeNotificationsAndVersion) {
        this.customerAsString = customerAsString;
        this.name = name;
        this.accountDetails = accountDetails;
        this.postedLoanDetails = new HashSet<>(postedLoanDetails);
        this.investedLoanDetails = new HashSet<>(investedLoanDetails);
        this.paymentNotificationsAndVersion = paymentNotificationsAndVersion;
        this.loanStatusChangeNotificationsAndVersion = loanStatusChangeNotificationsAndVersion;
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

    public Set<LoanDetails> getPostedLoanDetailsByStatus(LoanStatusData status) {
        return postedLoanDetails.stream().filter(loanDetails -> loanDetails.getStatus() == status).collect(Collectors.toSet());
    }

    public Set<LoanDetails> getInvestedLoanDetailsByStatus(LoanStatusData status) {
        return investedLoanDetails.stream().filter(loanDetails -> loanDetails.getStatus() == status).collect(Collectors.toSet());
    }

    public PaymentNotificationsAndVersion getPaymentNotificationsAndVersion() {
        return paymentNotificationsAndVersion;
    }

    public LoanStatusChangeNotificationsAndVersion getLoanStatusChangeNotificationsAndVersion() {
        return loanStatusChangeNotificationsAndVersion;
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
