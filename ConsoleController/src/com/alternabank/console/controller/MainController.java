package com.alternabank.console.controller;

import com.alternabank.console.ui.event.handler.xml.*;
import com.alternabank.engine.AlternaBankEngine;
import com.alternabank.engine.Engine;
import com.alternabank.engine.loan.Investment;
import com.alternabank.console.ui.AlternaBankUI;
import com.alternabank.console.ui.UI;
import com.alternabank.console.ui.event.handler.transaction.BilateralTransactionEventHandler;
import com.alternabank.console.ui.event.handler.transaction.UnilateralTransactionEventHandler;
import com.alternabank.console.ui.form.Form;
import com.alternabank.console.ui.form.OptionsForm;
import com.alternabank.console.ui.form.customer.CustomerDepositForm;
import com.alternabank.console.ui.form.customer.CustomerWithdrawalForm;
import com.alternabank.console.ui.form.main.MainForm;

import java.nio.file.Path;

public class MainController {

    public static void main(String[] args) {

        UI ui = AlternaBankUI.getInstance();
        Engine engine = AlternaBankEngine.getInstance();

        engine.addXMLFileLoadFailureListener(new XMLFileLoadFailureEventHandler());
        engine.addXMLCategoryLoadFailureListener(new XMLCategoryLoadFailureEventHandler());
        engine.addXMLCustomerLoadFailureListener(new XMLCustomerLoadFailureEventHandler());
        engine.addXMLLoanLoadFailureListener(new XMLLoanLoadFailureEventHandler());
        engine.addXMLLoadSuccessListener(new XMLLoadSuccessEventHandler());
        engine.addUnilateralTransactionListener(new UnilateralTransactionEventHandler());
        engine.addBilateralTransactionListener(new BilateralTransactionEventHandler());

        Form<Path> startingForm = ui.displayXMLLoadForm();
        if(startingForm.getResults() != null) {
            engine.loadFromXMLFile(startingForm.getResults());
            while(true) {
                ui.displayCurrentTimeMessage(engine.getCurrentTime(), engine.getTimeUnit());
                OptionsForm<MainForm.Option> mainForm = ui.displayMainForm();
                switch (mainForm.getResults()) {
                    case LOAD_XML_FROM_FILE:
                        engine.loadFromXMLFile(ui.displayXMLLoadForm().getResults());
                        break;
                    case DISPLAY_LOANS:
                        ui.displayLoanDetailsReport(engine.getLoanDetails());
                        break;
                    case DISPLAY_CUSTOMERS:
                        ui.displayCustomerDetailsReport(engine.getCustomerDetails());
                        break;
                    case DEPOSIT_FUNDS:
                        CustomerDepositForm.CustomerDepositRequest customerDepositRequest;
                        customerDepositRequest = ui.displayCustomerDepositForm(engine.getCustomerBalanceDetails()).getResults();
                        engine.customerDepositFunds(customerDepositRequest.getCustomerName(), customerDepositRequest.getTotal());
                        break;
                    case WITHDRAW_FUNDS:
                        CustomerWithdrawalForm.CustomerWithdrawalRequest customerWithdrawalRequest;
                        customerWithdrawalRequest = ui.displayCustomerWithdrawalForm(engine.getCustomerBalanceDetails()).getResults();
                        engine.customerWithdrawFunds(customerWithdrawalRequest.getCustomerName(), customerWithdrawalRequest.getTotal());
                        break;
                    case LOAN_ASSIGNMENT:
                        Investment.Request investmentRequest = ui.displayLoanInvestmentRequestForm(engine.getCustomerBalanceDetails(), engine.getLoanCategories(), engine.getLoanDetails()).getResults();
                        if(investmentRequest != null)
                            engine.postInvestmentRequest(investmentRequest);
                        break;
                    case ADVANCE_TIME:
                        engine.advanceTime();
                        break;
                    case EXIT:
                        engine.exit();
                        break;
                }
            }
        }
        else engine.exit();
    }

}
