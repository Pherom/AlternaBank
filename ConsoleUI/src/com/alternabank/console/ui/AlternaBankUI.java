package com.alternabank.console.ui;

import com.alternabank.console.ui.form.loan.LoanInvestmentRequestForm;
import com.alternabank.console.ui.message.time.CurrentTimeMessage;
import com.alternabank.engine.customer.dto.CustomerBalanceDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.console.ui.form.OptionsForm;
import com.alternabank.console.ui.form.YesNoForm;
import com.alternabank.console.ui.form.customer.CustomerDepositForm;
import com.alternabank.console.ui.form.Form;
import com.alternabank.console.ui.form.customer.CustomerWithdrawalForm;
import com.alternabank.console.ui.form.exit.ExitForm;
import com.alternabank.console.ui.form.main.MainForm;
import com.alternabank.console.ui.form.report.Report;
import com.alternabank.console.ui.form.report.customer.CustomerDetailsReport;
import com.alternabank.console.ui.form.report.loan.LoanDetailsReport;
import com.alternabank.console.ui.form.time.AdvanceTimeForm;
import com.alternabank.console.ui.form.xml.XMLLoadForm;
import com.alternabank.console.ui.message.Message;

import java.nio.file.Path;
import java.util.Set;

public class AlternaBankUI implements UI {

    private static AlternaBankUI instance = null;

    public static AlternaBankUI getInstance() {
        if(instance == null)
            instance = new AlternaBankUI();
        return instance;
    }

    private AlternaBankUI() {

    }

    @Override
    public Form<Path> displayXMLLoadForm() {
        Form<Path> xmlLoadForm = new XMLLoadForm();
        xmlLoadForm.display();
        return xmlLoadForm;
    }

    @Override
    public Form<CustomerDepositForm.CustomerDepositRequest> displayCustomerDepositForm(Set<CustomerBalanceDetails> customerBalanceDetails) {
        Form<CustomerDepositForm.CustomerDepositRequest> form = new CustomerDepositForm(customerBalanceDetails);
        form.display();
        return form;
    }

    @Override
    public Form<CustomerWithdrawalForm.CustomerWithdrawalRequest> displayCustomerWithdrawalForm(Set<CustomerBalanceDetails> customerBalanceDetails) {
        Form<CustomerWithdrawalForm.CustomerWithdrawalRequest> form = new CustomerWithdrawalForm(customerBalanceDetails);
        form.display();
        return form;
    }

    @Override
    public Form<Investment.Request> displayLoanInvestmentRequestForm(Set<CustomerBalanceDetails> customerBalanceDetails, Set<String> loanCategories, Set<LoanDetails> loanDetails) {
        Form<Investment.Request> form = new LoanInvestmentRequestForm(customerBalanceDetails, loanCategories, loanDetails);
        form.display();
        return form;
    }

    @Override
    public Report displayLoanDetailsReport(Set<LoanDetails> loanDetails) {
        Report report = new LoanDetailsReport(loanDetails);
        report.display();
        return report;
    }

    @Override
    public Report displayCustomerDetailsReport(Set<CustomerDetails> customerDetails) {
        Report report = new CustomerDetailsReport(customerDetails);
        report.display();
        return report;
    }

    @Override
    public OptionsForm<YesNoForm.Option> displayAdvanceTimeForm() {
        OptionsForm<YesNoForm.Option> form = new AdvanceTimeForm();
        form.display();
        return form;
    }

    @Override
    public OptionsForm<YesNoForm.Option> displayExitForm() {
        OptionsForm<YesNoForm.Option> form = new ExitForm();
        form.display();
        return form;
    }

    @Override
    public OptionsForm<MainForm.Option> displayMainForm() {
        OptionsForm<MainForm.Option> form = new MainForm();
        form.display();
        return form;
    }

    @Override
    public Message displayCurrentTimeMessage(int currentTime, String timeUnit) {
        Message currentTimeMessage = new CurrentTimeMessage(currentTime, timeUnit);
        currentTimeMessage.display();
        return currentTimeMessage;
    }

}
