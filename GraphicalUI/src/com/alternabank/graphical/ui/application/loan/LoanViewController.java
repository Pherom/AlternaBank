package com.alternabank.graphical.ui.application.loan;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Set;

public class LoanViewController implements Initializable {

    @FXML
    private TableColumn<LoanDetails, String> loanBorrowerTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> loanCapitalTableColumn;

    @FXML
    private TableColumn<LoanDetails, String> loanCategoryTableColumn;

    @FXML
    private ListView<?> loanDetailsListView;

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

    public void populate(Set<LoanDetails> loanDetails) {
        loanTableView.setItems(FXCollections.observableArrayList(loanDetails));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanBorrowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        loanCategoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanCapitalTableColumn.setCellValueFactory(new PropertyValueFactory<>("capital"));
        setLoanInterestTableColumnFactories(loanInterestTableColumn);
        loanTotalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        setLoanStatusTableColumnFactories(loanStatusTableColumn);
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

