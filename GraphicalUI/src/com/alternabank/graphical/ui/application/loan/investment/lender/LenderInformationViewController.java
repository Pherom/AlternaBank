package com.alternabank.graphical.ui.application.loan.investment.lender;

import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.Investment;
import com.alternabank.engine.loan.dto.LoanDetails;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LenderInformationViewController implements Initializable {

    @FXML private TableView<Map.Entry<String, Double>> lenderInformationTableView;

    @FXML private VBox lenderInformationViewComponent;

    @FXML private TableColumn<Map.Entry<String, Double>, Double> lenderInvestmentTableColumn;

    @FXML private TableColumn<Map.Entry<String, Double>, String> lenderNameTableColumn;

    public void populate(LoanDetails loanDetails) {
        lenderInformationTableView.setItems(FXCollections.observableList(new ArrayList<>(loanDetails.getInvestmentByLenderName().entrySet())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lenderNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        lenderInvestmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
    }
}
