package com.alternabank.graphical.ui.application.loan;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.pending.PendingLoanInformationViewController;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class LoanViewController implements Initializable {

    private final ListProperty<LoanDetails> loanDetails = new SimpleListProperty<>();

    @FXML
    private TableColumn<LoanDetails, String> loanBorrowerTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> loanCapitalTableColumn;

    @FXML
    private TableColumn<LoanDetails, String> loanCategoryTableColumn;

    @FXML
    private TableColumn<LoanDetails, String> loanIDTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> loanInterestTableColumn;

    @FXML
    private TableColumn<LoanDetails, Loan.Status> loanStatusTableColumn;

    @FXML
    private TableView<LoanDetails> loanTableView;

    @FXML
    private TableColumn<LoanDetails, Double> loanTotalTableColumn;

    @FXML
    private Tab activeStageInformationTab;

    @FXML
    private Tab riskStageInformationTab;

    @FXML
    private Tab finishedStageInformationTab;

    @FXML
    private PendingLoanInformationViewController pendingLoanInformationViewComponentController;

    public ListProperty<LoanDetails> loanDetailsProperty() {
        return loanDetails;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanTableView.itemsProperty().bind(loanDetails);
        loanIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanBorrowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        loanCategoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanCapitalTableColumn.setCellValueFactory(new PropertyValueFactory<>("capital"));
        setLoanInterestTableColumnFactories(loanInterestTableColumn);
        loanTotalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        setLoanStatusTableColumnFactories(loanStatusTableColumn);
        loanTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LoanDetails>() {
            @Override
            public void changed(ObservableValue<? extends LoanDetails> observable, LoanDetails oldValue, LoanDetails newValue) {
                if(newValue != null)
                    onLoanSelected(newValue);
            }
        });
    }

    private void makeTabsAvailableBasedOnLoanStatus(Loan.Status status) {
        activeStageInformationTab.setDisable(true);
        riskStageInformationTab.setDisable(true);
        finishedStageInformationTab.setDisable(true);

        switch (status) {
            case RISK:
                riskStageInformationTab.setDisable(false);
                activeStageInformationTab.setDisable(false);
                break;
            case FINISHED:
                finishedStageInformationTab.setDisable(false);
            case ACTIVE:
                activeStageInformationTab.setDisable(false);
                break;
        }
    }

    public void onLoanSelected(LoanDetails selectedLoanDetails) {
        makeTabsAvailableBasedOnLoanStatus(selectedLoanDetails.getStatus());
        pendingLoanInformationViewComponentController.populate(selectedLoanDetails);
    }

    private void setLoanInterestTableColumnFactories(TableColumn<LoanDetails, Double> loanInterestTableColumn) {
        loanInterestTableColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        loanInterestTableColumn.setCellFactory(tc -> new TableCell<LoanDetails, Double>() {

            @Override
            protected void updateItem(Double interest, boolean empty) {
                super.updateItem(interest, empty);
                if (!empty)
                    setText(String.format("%.2f%%", interest * 100));
                else setText(null);
            }
        });
    }

    private void setLoanStatusTableColumnFactories(TableColumn<LoanDetails, Loan.Status> loanStatusTableColumn) {
        loanStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        loanStatusTableColumn.setCellFactory(tc -> new TableCell<LoanDetails, Loan.Status>() {

            @Override
            protected void updateItem(Loan.Status status, boolean empty) {
                super.updateItem(status, empty);
                this.getStyleClass().add("status-cell");
                if(!empty) {
                    setText(status.toString());
                    switch(status) {
                        case PENDING:
                            pendingStatusUpdated(this);
                            break;
                        case ACTIVE:
                            activeStatusUpdated(this);
                            break;
                        case RISK:
                            riskStatusUpdated(this);
                            break;
                        case FINISHED:
                            finishedStatusUpdated(this);
                            break;
                    }
                }
                else {
                    setText(null);
                    resetStatus(this);
                }
            }
        });
    }

    private void pendingStatusUpdated(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), true);
    }

    private void activeStatusUpdated(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
    }

    private void riskStatusUpdated(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), true);
    }

    private void finishedStatusUpdated(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("finished"), true);
    }

    private void resetStatus(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("finished"), false);
    }
}

