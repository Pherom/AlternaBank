package com.alternabank.client.user.information;

import com.alternabank.client.app.AppController;
import com.alternabank.client.loan.LoanViewController;
import com.alternabank.client.user.UserViewController;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.dto.loan.InvestmentDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuyInvestmentsFormController implements Initializable {

    private UserViewController userViewComponentController;

    @FXML private LoanViewController loanViewComponentController;

    @FXML private TableView<Map.Entry<String, List<InvestmentDetails>>> investmentsAvailableTableView;

    @FXML private TableColumn<Map.Entry<String, List<InvestmentDetails>>, String> sellerNameTableColumn;

    @FXML private TableColumn<Map.Entry<String, List<InvestmentDetails>>, Double> priceTableColumn;

    @FXML private Button purchaseSelectedButton;

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        loanViewComponentController.loanDetailsProperty().set(new FilteredList<>(LoanDetailsUtil.loanDetails,
                loanDetails -> loanDetails.getStatus() == LoanStatusData.ACTIVE
                        && !loanDetails.getBorrowerName().equals(userViewComponentController.getAppController().getUsername())
                        && loanDetails.getInvestmentsByLenderName().values().stream()
                        .anyMatch(investmentDetailsList -> investmentDetailsList.stream()
                                .anyMatch(investmentDetails -> investmentDetails.getForSale()
                                        && !investmentDetails.getLenderName().equals(userViewComponentController.getAppController().getUsername())))));
    }

    @FXML
    private void onPurchaseSelectedRequest(ActionEvent event) throws IOException {
        Map.Entry<String, List<InvestmentDetails>> investmentsBySellerEntry = investmentsAvailableTableView.getSelectionModel().getSelectedItem();
        if (investmentsBySellerEntry != null) {
            if (priceTableColumn.getCellData(investmentsBySellerEntry) > userViewComponentController.getAccountBalance()) {
                showPurchaseErrorAlert("You do not have enought funds to complete this purchase");
            }
            else {
                String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/buy-investment")
                        .newBuilder().addQueryParameter("seller", investmentsBySellerEntry.getKey())
                        .addQueryParameter("loan", loanViewComponentController.getSelectedLoanDetails().getId())
                        .build().toString();
                Response response = HttpClientUtil.runPutSync(finalUrl, RequestBody.create(new byte[0], null));
                if (response.code() == 200) {
                    showPurchaseSuccessAlert();
                } else {
                    showPurchaseErrorAlert("Something went wrong, we could not submit your purchase request :(");
                }
            }
        }
        else {
            showMissingInvestmentSelectionErrorAlert();
        }
    }

    private void showPurchaseSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("Your purchase came through!");
        alert.setContentText("You are now the proud owner of this investment :)");
        alert.showAndWait();
    }

    private void showPurchaseErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Could not perform investment purchase");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMissingInvestmentSelectionErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Missing investment for purchase!");
        alert.setContentText("Please select an investment you would like to purchase to continue");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanViewComponentController.selectedLoanDetailsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                investmentsAvailableTableView.setItems(FXCollections.observableList(newValue.getInvestmentsByLenderName().entrySet().stream()
                        .filter(entry -> entry.getValue().stream()
                                .anyMatch(InvestmentDetails::getForSale)).collect(Collectors.toList())));
            else if (!loanViewComponentController.loanDetailsProperty().contains(oldValue))
                investmentsAvailableTableView.getItems().clear();
        });
        sellerNameTableColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        priceTableColumn.setCellValueFactory(param -> new SimpleDoubleProperty(loanViewComponentController.getSelectedLoanDetails().getLenderRemainingPrincipalPortion(param.getValue().getKey())).asObject());
        investmentsAvailableTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                boolean oldValueStillExists = investmentsAvailableTableView.getItems().contains(oldValue);
                if (oldValueStillExists) {
                    investmentsAvailableTableView.getSelectionModel().select(investmentsAvailableTableView.getItems().indexOf(oldValue));
                }
                purchaseSelectedButton.setDisable(!oldValueStillExists);
            }
            else purchaseSelectedButton.setDisable(false);
        });
    }
}
