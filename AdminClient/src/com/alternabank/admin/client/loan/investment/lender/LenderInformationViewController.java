package com.alternabank.admin.client.loan.investment.lender;

import com.alternabank.admin.client.loan.investment.InvestmentInformationViewController;
import com.alternabank.client.util.DoubleCellFactory;
import com.alternabank.dto.loan.InvestmentDetails;
import com.alternabank.dto.loan.LoanDetails;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LenderInformationViewController implements Initializable {

    private InvestmentInformationViewController investmentInformationViewComponentController;

    @FXML private TableView<List<InvestmentDetails>> lenderInformationTableView;

    @FXML private VBox lenderInformationViewComponent;

    @FXML private TableColumn<List<InvestmentDetails>, Double> lenderInvestmentTableColumn;

    @FXML private TableColumn<List<InvestmentDetails>, String> lenderNameTableColumn;

    public void setInvestmentInformationViewComponentController(InvestmentInformationViewController controller) {
        investmentInformationViewComponentController = controller;
    }

    public void onLoanSelected() {
        LoanDetails selectedLoanDetails = investmentInformationViewComponentController.getPendingLoanInformationViewController().getLoanViewController().getSelectedLoanDetails();
        lenderInformationTableView.setItems(FXCollections.observableList(new ArrayList<>(selectedLoanDetails.getInvestmentsByLenderName().values())));
    }

    public void onLoanDeselected() {
        lenderInformationTableView.getItems().clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lenderNameTableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0).getLenderName()));
        lenderInvestmentTableColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().stream().mapToDouble(InvestmentDetails::getInvestmentTotal).sum()).asObject());
        lenderInvestmentTableColumn.setCellFactory(new DoubleCellFactory<>());
    }
}
