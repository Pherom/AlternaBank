package com.alternabank.graphical.ui.application.customer;

import com.alternabank.engine.account.dto.AccountDetails;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

public class CustomerViewController implements Initializable {

    private ListProperty<CustomerDetails> customerDetails = new SimpleListProperty<>();

    @FXML
    private TableColumn<CustomerDetails, Number> activeBorrowedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> activeLendedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> customerBalanceTableColumn;

    @FXML
    private TableColumn<CustomerDetails, String> customerNameTableColumn;

    @FXML
    private TableView<CustomerDetails> customerViewComponent;

    @FXML
    private TableColumn<CustomerDetails, Number> finishedBorrowedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> finishedLendedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> pendingBorrowedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> pendingLendedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> riskBorrowedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> riskLendedLoansTableColumn;

    public ListProperty<CustomerDetails> customerDetailsProperty() {
        return customerDetails;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerViewComponent.itemsProperty().bind(customerDetails);
        customerNameTableColumn.setCellValueFactory(new PropertyValueFactory<CustomerDetails, String>("name"));
        customerBalanceTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAccountDetails().getBalance()));
        pendingBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(Loan.Status.PENDING).size()));
        activeBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(Loan.Status.ACTIVE).size()));
        riskBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(Loan.Status.RISK).size()));
        finishedBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(Loan.Status.FINISHED).size()));
        pendingLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(Loan.Status.PENDING).size()));
        activeLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(Loan.Status.ACTIVE).size()));
        riskLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(Loan.Status.RISK).size()));
        finishedLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(Loan.Status.FINISHED).size()));

        customerBalanceTableColumn.setCellFactory(tc -> new TableCell<CustomerDetails, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else setText(String.format("%.2f", item.doubleValue()));
            }
        });


    }
}
