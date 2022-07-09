package com.alternabank.admin.client.customer;

import com.alternabank.dto.customer.CustomerDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {

    private ListProperty<CustomerDetails> customerDetails = new SimpleListProperty<>(FXCollections.observableArrayList());

    @FXML
    private TableView<CustomerDetails> customerViewComponent;

    @FXML
    private TableColumn<CustomerDetails, Number> activeBorrowedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> activeLendedLoansTableColumn;

    @FXML
    private TableColumn<CustomerDetails, Number> customerBalanceTableColumn;

    @FXML
    private TableColumn<CustomerDetails, String> customerNameTableColumn;

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

    public void setCustomerDetails(Map<String, CustomerDetails> customerDetails) {
        this.customerDetails.setAll(customerDetails.values());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerViewComponent.itemsProperty().bind(customerDetails);
        customerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerBalanceTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAccountDetails().getBalance()));
        pendingBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(LoanStatusData.PENDING).size()));
        activeBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(LoanStatusData.ACTIVE).size()));
        riskBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(LoanStatusData.RISK).size()));
        finishedBorrowedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPostedLoanDetailsByStatus(LoanStatusData.FINISHED).size()));
        pendingLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(LoanStatusData.PENDING).size()));
        activeLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(LoanStatusData.ACTIVE).size()));
        riskLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(LoanStatusData.RISK).size()));
        finishedLendedLoansTableColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInvestedLoanDetailsByStatus(LoanStatusData.FINISHED).size()));

        customerBalanceTableColumn.setCellFactory(tc -> new TableCell<CustomerDetails, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else setText(String.format("%.2f", item.doubleValue()));
            }
        });

        customerViewComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && customerViewComponent.getItems().contains(oldValue))
                customerViewComponent.getSelectionModel().select(customerViewComponent.getItems().indexOf(oldValue));
        });
    }
}
