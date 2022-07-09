package com.alternabank.client.util.loan;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoanCategoriesUtil {

    private static TimerTask loanCategoriesRefresher;

    private static Timer timer;

    public static final ListProperty<String> loanCategories = new SimpleListProperty<>();

    private static void updateLoanCategories(List<String> loanCategories) {
        if(loanCategories.size() != LoanCategoriesUtil.loanCategories.size())
            Platform.runLater(() -> LoanCategoriesUtil.loanCategories.set(FXCollections.observableList(loanCategories)));
    }

    public static void startLoanCategoriesRefresher() {
        loanCategoriesRefresher = new LoanCategoriesRefresher(
                LoanCategoriesUtil::updateLoanCategories);
        timer = new Timer();
        timer.schedule(loanCategoriesRefresher, 500, 500);
    }

}
