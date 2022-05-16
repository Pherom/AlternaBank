package com.alternabank.graphical.ui.application.loan;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.NumberFormat;
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
        loanTableView.getItems().addAll(loanDetails);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanBorrowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        loanCategoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanCapitalTableColumn.setCellValueFactory(new PropertyValueFactory<>("capital"));
        loanInterestTableColumn.setCellFactory(tc -> new TableCell<LoanDetails, Double>() {

            @Override
            protected void updateItem(Double interest, boolean empty) {
                super.updateItem(interest, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", interest * 100));
                }
            }
        });
        loanInterestTableColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        loanTotalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        loanStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
}

