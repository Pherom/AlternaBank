package com.alternabank.engine.loan.task;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.request.InvestmentRequestBuilder;
import com.alternabank.engine.user.UserManager;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.Collection;
import java.util.Set;

public class InvestmentTask extends Task<Boolean> {

    private final String lenderName;
    private final double investmentTotal;
    private final double minimumInterest;
    private final int maximumLoanOwnershipPercentage;
    private final int minimumLoanTerm;
    private final int maximumBorrowerActiveLoans;
    private final Set<String> categoriesOfInterest;
    private final Collection<String> loansToInvestIn;

    public InvestmentTask(String lenderName, double investmentTotal, double minimumInterest, int maximumLoanOwnershipPercentage, int minimumLoanTerm, int maximumBorrowerActiveLoans, Set<String> categoriesOfInterest, Collection<String> loansToInvestIn) {
        this.lenderName = lenderName;
        this.investmentTotal = investmentTotal;
        this.maximumLoanOwnershipPercentage = maximumLoanOwnershipPercentage;
        this.minimumInterest = minimumInterest;
        this.categoriesOfInterest = categoriesOfInterest;
        this.minimumLoanTerm = minimumLoanTerm;
        this.maximumBorrowerActiveLoans = maximumBorrowerActiveLoans;
        this.loansToInvestIn = loansToInvestIn;
    }

    private void fakeProgress() throws InterruptedException {
        Thread.sleep(300);
    }

    @Override
    protected Boolean call() throws Exception {
        updateProgress(0, 10);
        updateMessage("Initiating investment request builder...");
        fakeProgress();
        InvestmentRequestBuilder investmentRequestBuilder = new InvestmentRequestBuilder(lenderName, investmentTotal);
        updateProgress(1, 10);
        updateMessage("Updating maximum loan ownership percentage...");
        fakeProgress();
        investmentRequestBuilder.setMaximumLoanOwnershipPercentage(maximumLoanOwnershipPercentage);
        updateProgress(2, 10);
        updateMessage("Updating minimum interest...");
        fakeProgress();
        investmentRequestBuilder.setMinimumInterest(minimumInterest);
        updateProgress(3, 10);
        updateMessage("Updating categories of interest...");
        fakeProgress();
        investmentRequestBuilder.setCategoriesOfInterest(categoriesOfInterest);
        updateProgress(4, 10);
        updateMessage("Updating minimum loan term...");
        fakeProgress();
        investmentRequestBuilder.setMinimumLoanTerm(minimumLoanTerm);
        updateProgress(5, 10);
        updateMessage("Updating maximum borrower active loans...");
        fakeProgress();
        investmentRequestBuilder.setMaximumBorrowerActiveLoans(maximumBorrowerActiveLoans);
        updateProgress(6, 10);
        updateMessage("Adding chosen loan IDs...");
        fakeProgress();
        investmentRequestBuilder.addLoansToInvestIn(loansToInvestIn);
        updateProgress(7, 10);
        fakeProgress();
        updateMessage("Creating investment request from builder...");
        Investment.Request investmentRequest = investmentRequestBuilder.build();
        updateProgress(8, 10);
        fakeProgress();
        updateMessage("Investment request created successfully!");
        fakeProgress();
        updateMessage("Fetching customer from lender name...");
        fakeProgress();
        CustomerManager.Customer lender = UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(lenderName);
        updateProgress(9, 10);
        updateMessage("Posting investment request...");
        fakeProgress();
        Platform.runLater(() -> lender.postInvestmentRequest(investmentRequest));
        updateProgress(10, 10);
        updateMessage("Investment posted!");
        return Boolean.TRUE;
    }


}
