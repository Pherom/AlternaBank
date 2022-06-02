package com.alternabank.graphical.ui.application.loan;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.active.ActiveLoanInformationViewController;
import com.alternabank.graphical.ui.application.loan.finished.FinishedLoanInformationViewController;
import com.alternabank.graphical.ui.application.loan.pending.PendingLoanInformationViewController;
import com.alternabank.graphical.ui.application.loan.risk.RiskLoanInformationViewController;
import com.alternabank.graphical.ui.application.util.DoubleCellFactory;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoanViewController implements Initializable {

    private final ObjectProperty<LoanDetails> selectedLoanDetails = new SimpleObjectProperty<>();

    private final ListProperty<LoanDetails> loanDetails = new SimpleListProperty<>();

    @FXML
    private SplitPane loanViewComponent;

    @FXML
    private TableColumn<LoanDetails, String> loanBorrowerTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> loanCapitalTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> principalPerInstallmentTableColumn;

    @FXML
    private TableColumn<LoanDetails, String> loanCategoryTableColumn;

    @FXML
    private TableColumn<LoanDetails, String> loanIDTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> interestRateTableColumn;

    @FXML
    TableColumn<LoanDetails, Double> interestTotalTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> interestPerInstallmentTableColumn;

    @FXML
    private TableColumn<LoanDetails, Loan.Status> loanStatusTableColumn;

    @FXML
    private TableView<LoanDetails> loanTableView;

    @FXML
    private TableColumn<LoanDetails, Integer> installmentPeriodTableColumn;

    @FXML
    private TableColumn<LoanDetails, Integer> loanTermTableColumn;

    @FXML
    private TableColumn<LoanDetails, Double> loanTotalTableColumn;

    @FXML
    private TabPane loanAdditionalDetailsTabPane;

    @FXML
    private Tab pendingStageInformationTab;

    @FXML
    private Tab activeStageInformationTab;

    @FXML
    private Tab riskStageInformationTab;

    @FXML
    private Tab finishedStageInformationTab;

    @FXML
    private PendingLoanInformationViewController pendingLoanInformationViewComponentController;

    @FXML
    private ActiveLoanInformationViewController activeLoanInformationViewComponentController;

    @FXML
    private RiskLoanInformationViewController riskLoanInformationViewComponentController;

    @FXML
    private FinishedLoanInformationViewController finishedLoanInformationViewComponentController;

    public ListProperty<LoanDetails> loanDetailsProperty() {
        return loanDetails;
    }

    public ObjectProperty<LoanDetails> selectedLoanDetailsProperty() {
        return selectedLoanDetails;
    }

    public LoanDetails getSelectedLoanDetails() {
        return selectedLoanDetails.get();
    }

    public void setLoanDetails(List<LoanDetails> loanDetails) {
        this.loanDetailsProperty().set(FXCollections.observableList(loanDetails));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pendingLoanInformationViewComponentController.setLoanViewController(this);
        activeLoanInformationViewComponentController.setLoanViewController(this);
        riskLoanInformationViewComponentController.setLoanViewController(this);
        finishedLoanInformationViewComponentController.setLoanViewController(this);
        DoubleCellFactory<LoanDetails> doubleCellFactory = new DoubleCellFactory<>();
        loanTableView.itemsProperty().bind(loanDetails);
        loanIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanBorrowerTableColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        loanCategoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        loanCapitalTableColumn.setCellValueFactory(new PropertyValueFactory<>("capital"));
        loanCapitalTableColumn.setCellFactory(doubleCellFactory);
        principalPerInstallmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("principalPerInstallment"));
        principalPerInstallmentTableColumn.setCellFactory(doubleCellFactory);
        setLoanInterestRateTableColumnFactories(interestRateTableColumn);
        interestTotalTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalInterest"));
        interestTotalTableColumn.setCellFactory(doubleCellFactory);
        interestPerInstallmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("interestPerInstallment"));
        interestPerInstallmentTableColumn.setCellFactory(doubleCellFactory);
        installmentPeriodTableColumn.setCellValueFactory(new PropertyValueFactory<>("installmentPeriod"));
        loanTermTableColumn.setCellValueFactory(new PropertyValueFactory<>("originalTerm"));
        loanTotalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        loanTotalTableColumn.setCellFactory(doubleCellFactory);
        setLoanStatusTableColumnFactories(loanStatusTableColumn);
        selectedLoanDetailsProperty().bind(loanTableView.getSelectionModel().selectedItemProperty());
        loanTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)
                onLoanSelected();
            else onLoanDeselected();
        });
    }

    private void disableAllButPendingStageInformationTab() {
        activeStageInformationTab.setDisable(true);
        riskStageInformationTab.setDisable(true);
        finishedStageInformationTab.setDisable(true);
    }

    private void makeTabsAvailableBasedOnLoanStatus(Loan.Status status) {
        disableAllButPendingStageInformationTab();

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

    public void onLoanSelected() {
        makeTabsAvailableBasedOnLoanStatus(selectedLoanDetails.get().getStatus());
        if (loanAdditionalDetailsTabPane.getSelectionModel().getSelectedItem().isDisable())
            loanAdditionalDetailsTabPane.getSelectionModel().select(pendingStageInformationTab);
        pendingLoanInformationViewComponentController.onLoanSelected();
        activeLoanInformationViewComponentController.onLoanSelected();
        riskLoanInformationViewComponentController.onLoanSelected();
        finishedLoanInformationViewComponentController.onLoanSelected();
    }

    public void onLoanDeselected() {
        disableAllButPendingStageInformationTab();
        loanAdditionalDetailsTabPane.getSelectionModel().select(pendingStageInformationTab);
        pendingLoanInformationViewComponentController.onLoanDeselected();
    }

    private void setLoanInterestRateTableColumnFactories(TableColumn<LoanDetails, Double> loanInterestTableColumn) {
        loanInterestTableColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        loanInterestTableColumn.setCellFactory(tc -> new TableCell<LoanDetails, Double>() {

            @Override
            protected void updateItem(Double interest, boolean empty) {
                super.updateItem(interest, empty);
                if (!empty) {
                    setText(String.format("%.2f%%", interest * 100));
                }
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
                resetStatus(this);
                if(!empty) {
                    setText(status.toString());
                    switch(status) {
                        case PENDING:
                            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), true);
                            break;
                        case ACTIVE:
                            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
                            break;
                        case RISK:
                            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), true);
                            break;
                        case FINISHED:
                            this.pseudoClassStateChanged(PseudoClass.getPseudoClass("finished"), true);
                            break;
                    }
                }
                else {
                    setText(null);
                }
            }
        });
    }

    private void resetStatus(TableCell<LoanDetails, Loan.Status> statusCell) {
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("risk"), false);
        statusCell.pseudoClassStateChanged(PseudoClass.getPseudoClass("finished"), false);
    }
}

