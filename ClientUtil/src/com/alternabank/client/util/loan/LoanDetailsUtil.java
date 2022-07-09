package com.alternabank.client.util.loan;

import com.alternabank.dto.loan.LoanDetails;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoanDetailsUtil {

    private static TimerTask loanDetailsRefresher;

    private static Timer timer;

    public static final ListProperty<LoanDetails> loanDetails = new SimpleListProperty<>();

    private static void updateLoanDetails(Map<String, LoanDetails> loanDetails) {
        Platform.runLater(() -> LoanDetailsUtil.loanDetails.set(FXCollections.observableArrayList(loanDetails.values())));
    }

    public static void startLoanDetailsRefresher() {
        loanDetailsRefresher = new LoanDetailsRefresher(
                LoanDetailsUtil::updateLoanDetails);
        timer = new Timer();
        timer.schedule(loanDetailsRefresher, 500, 500);
    }

}
