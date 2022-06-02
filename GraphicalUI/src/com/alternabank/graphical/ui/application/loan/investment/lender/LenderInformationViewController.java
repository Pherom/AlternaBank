package com.alternabank.graphical.ui.application.loan.investment.lender;

import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.graphical.ui.application.loan.investment.InvestmentInformationViewController;
import com.alternabank.graphical.ui.application.util.DoubleCellFactory;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LenderInformationViewController implements Initializable {

    private InvestmentInformationViewController investmentInformationViewComponentController;

    @FXML private TableView<Map.Entry<String, Double>> lenderInformationTableView;

    @FXML private VBox lenderInformationViewComponent;

    @FXML private TableColumn<Map.Entry<String, Double>, Double> lenderInvestmentTableColumn;

    @FXML private TableColumn<Map.Entry<String, Double>, String> lenderNameTableColumn;

    public void setInvestmentInformationViewComponentController(InvestmentInformationViewController controller) {
        investmentInformationViewComponentController = controller;
    }

    public void onLoanSelected() {
        LoanDetails selectedLoanDetails = investmentInformationViewComponentController.getPendingLoanInformationViewController().getLoanViewController().getSelectedLoanDetails();
        lenderInformationTableView.setItems(FXCollections.observableList(new ArrayList<>(selectedLoanDetails.getInvestmentByLenderName().entrySet())));
    }

    public void onLoanDeselected() {
        lenderInformationTableView.getItems().clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lenderNameTableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        lenderInvestmentTableColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue()).asObject());
        lenderInvestmentTableColumn.setCellFactory(new DoubleCellFactory<>());
    }
}
