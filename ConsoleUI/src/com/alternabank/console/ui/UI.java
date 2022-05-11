package com.alternabank.console.ui;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.console.ui.form.OptionsForm;
import com.alternabank.console.ui.form.YesNoForm;
import com.alternabank.console.ui.form.customer.CustomerDepositForm;
import com.alternabank.console.ui.form.Form;
import com.alternabank.console.ui.form.customer.CustomerWithdrawalForm;
import com.alternabank.console.ui.form.main.MainForm;
import com.alternabank.console.ui.form.report.Report;
import com.alternabank.console.ui.message.Message;

import java.nio.file.Path;
import java.util.Set;

public interface UI {

    Form<Path> displayXMLLoadForm();

    Form<CustomerDepositForm.CustomerDepositRequest> displayCustomerDepositForm(Set<CustomerBalanceDetails> customerBalanceDetails);

    Form<CustomerWithdrawalForm.CustomerWithdrawalRequest> displayCustomerWithdrawalForm(Set<CustomerBalanceDetails> customerBalanceDetails);

    Form<Investment.Request> displayLoanInvestmentRequestForm(Set<CustomerBalanceDetails> customerBalanceDetails, Set<String> loanCategories, Set<LoanDetails> loanDetails);

    Report displayLoanDetailsReport(Set<LoanDetails> loanDetails);

    Report displayCustomerDetailsReport(Set<CustomerDetails> customerDetails);

    OptionsForm<YesNoForm.Option> displayAdvanceTimeForm();

    OptionsForm<YesNoForm.Option> displayExitForm();

    OptionsForm<MainForm.Option> displayMainForm();

    Message displayCurrentTimeMessage(int currentTime, String timeUnit);
}
