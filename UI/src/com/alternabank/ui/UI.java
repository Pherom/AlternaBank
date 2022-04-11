package com.alternabank.ui;

import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.ui.form.OptionsForm;
import com.alternabank.ui.form.YesNoForm;
import com.alternabank.ui.form.customer.CustomerDepositForm;
import com.alternabank.ui.form.Form;
import com.alternabank.ui.form.customer.CustomerWithdrawalForm;
import com.alternabank.ui.form.main.MainForm;
import com.alternabank.ui.form.report.Report;
import com.alternabank.ui.message.Message;

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
