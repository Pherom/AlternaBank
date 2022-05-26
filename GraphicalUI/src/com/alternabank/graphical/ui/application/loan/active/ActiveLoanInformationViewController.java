package com.alternabank.graphical.ui.application.loan.active;

import com.alternabank.engine.transaction.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ActiveLoanInformationViewController {

    @FXML private Label activeSinceLabel;

    @FXML private Label nextInstallmentLabel;

    @FXML private Label paidPrincipalLabel;

    @FXML private Label paidInterestLabel;

    @FXML private Label remainingInterestLabel;

    @FXML private Label remainingPrincipalLabel;

    @FXML private ListView<Transaction.Record> accountLedgerListView;

}
