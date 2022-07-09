package com.alternabank.client.user.investment;

import com.alternabank.client.user.UserViewController;
import com.alternabank.client.util.DoubleTextFormatter;
import com.alternabank.client.util.http.HttpClientUtil;
import com.alternabank.client.loan.LoanViewController;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.client.util.loan.LoanCategoriesUtil;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.dto.loan.request.InvestmentRequest;
import com.alternabank.dto.loan.request.InvestmentRequestBuilder;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.controlsfx.control.CheckListView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserViewInvestmentController implements Initializable {

    private final ListProperty<LoanDetails> selectedLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<LoanDetails> matchingLoanDetails = new SimpleListProperty<>(FXCollections.observableArrayList());
    private UserViewController userViewComponentController;

    @FXML private CheckListView<String> loanCategoryCheckListView;

    @FXML private TextField loanInvestmentTotalTextField;

    @FXML private TextField loanMinimumInterestTextField;

    @FXML private LoanViewController matchingLoanViewComponentController;

    @FXML private LoanViewController selectedLoanViewComponentController;

    @FXML private Spinner<Integer> loanMinTermSpinner;

    @FXML private Spinner<Integer> borrowerMaxLoansSpinner;

    @FXML private Spinner<Integer> loanMaxOwnershipRateSpinner;

    @FXML private Button investButton;

    @FXML private HBox loanSelectionControlsArea;

    @FXML private VBox filtersAndInvestmentInfoArea;

    private final ValidationSupport validationSupport = new ValidationSupport();

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        validationSupport.registerValidator(loanInvestmentTotalTextField, Validator.createPredicateValidator((String text) -> {
            Double textAsDouble = text.isEmpty() ? null : Double.parseDouble(text);
            return textAsDouble != null && textAsDouble > 0 && textAsDouble <= userViewComponentController.getAccountBalance();
        }, "Total investment must be higher than 0 and lower than account balance"));
        validationSupport.initInitialDecoration();
    }

    private void prepareForNewInvestmentRequest() {
        loanCategoryCheckListView.getCheckModel().checkAll();
        loanInvestmentTotalTextField.clear();
        loanMinimumInterestTextField.clear();
        loanMinTermSpinner.getValueFactory().setValue(1);
        borrowerMaxLoansSpinner.getValueFactory().setValue(20);
        loanMaxOwnershipRateSpinner.getValueFactory().setValue(100);
        selectedLoanDetails.clear();
        updateMatchingLoansAccordingToFilter();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanCategoryCheckListView.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> onFilterUpdateRequest());
        loanMinTermSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onFilterUpdateRequest());
        borrowerMaxLoansSpinner.valueProperty().addListener((observable, oldValue, newValue) -> onFilterUpdateRequest());
        loanMaxOwnershipRateSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 100));
        loanInvestmentTotalTextField.setTextFormatter(new DoubleTextFormatter(null));
        loanMinimumInterestTextField.setTextFormatter(new DoubleTextFormatter(null));
        loanMinimumInterestTextField.textProperty().addListener((observable, oldValue, newValue) -> onFilterUpdateRequest());
        validationSupport.registerValidator(loanInvestmentTotalTextField, true, Validator.createEmptyValidator("Total investment is required"));
        validationSupport.registerValidator(loanMinimumInterestTextField, false, Validator.createPredicateValidator((String text) -> text.isEmpty() || Double.parseDouble(text) > 0, "Minimum interest rate must be higher than 0"));
        matchingLoanViewComponentController.loanDetailsProperty().bind(matchingLoanDetails);
        selectedLoanViewComponentController.loanDetailsProperty().bind(selectedLoanDetails);
        LoanCategoriesUtil.loanCategories.addListener((observable, oldValue, newValue) -> loanCategoryCheckListView.getItems().setAll(newValue));
        loanMinTermSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        borrowerMaxLoansSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 20));
        loanCategoryCheckListView.getCheckModel().checkAll();
        filtersAndInvestmentInfoArea.disableProperty().bind(ServerTimeUtil.rewindMode);
        loanSelectionControlsArea.disableProperty().bind(ServerTimeUtil.rewindMode);
        investButton.disableProperty().bind(ServerTimeUtil.rewindMode);
    }

    private void updateMatchingLoansAccordingToFilter() {
        matchingLoanDetails.setAll(LoanDetailsUtil.loanDetails.stream().filter(loanDetails ->
                selectedLoanDetails.stream().noneMatch(selectedLoanDetails -> loanDetails.getId().equals(selectedLoanDetails.getId())) &&
                        !loanDetails.getBorrowerName().equals(userViewComponentController.getAppController().getUsername()) &&
                        loanDetails.getStatus() == LoanStatusData.PENDING &&
                        loanCategoryCheckListView.getCheckModel().getCheckedItems().contains(loanDetails.getCategory()) &&
                        (loanMinimumInterestTextField.getText().isEmpty() || loanDetails.getTotalInterest() >= Double.parseDouble(loanMinimumInterestTextField.getText())) &&
                        loanDetails.getOriginalTerm() >= loanMinTermSpinner.getValue() &&
                        loanDetails.getBorrowerActiveLoanCount() <= borrowerMaxLoansSpinner.getValue())
                .sorted(Comparator.comparing(LoanDetails::getId, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
    }

    private void onFilterUpdateRequest() {
        updateMatchingLoansAccordingToFilter();
    }

    @FXML private void onRemoveAllLoansRequest(ActionEvent event) {
        matchingLoanDetails.addAll(selectedLoanDetails);
        selectedLoanDetails.clear();
    }

    @FXML private void onLoanSelectionRemoveRequest(ActionEvent event) {
        if(selectedLoanViewComponentController.getSelectedLoanDetails() != null) {
            matchingLoanDetails.add(selectedLoanViewComponentController.getSelectedLoanDetails());
            selectedLoanDetails.remove(selectedLoanViewComponentController.getSelectedLoanDetails());
        }
    }

    @FXML private void onAddAllLoansRequest(ActionEvent event) {
        selectedLoanDetails.addAll(matchingLoanDetails);
        matchingLoanDetails.clear();
    }

    @FXML private void onLoanSelectionAddRequest(ActionEvent event) {
        if(matchingLoanViewComponentController.getSelectedLoanDetails() != null) {
            selectedLoanDetails.add(matchingLoanViewComponentController.getSelectedLoanDetails());
            matchingLoanDetails.remove(matchingLoanViewComponentController.getSelectedLoanDetails());
        }
    }

    private void showMissingInvestmentTotalAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Missing Investment Total!");
        alert.setContentText("Investment total is required to proceed");
        alert.showAndWait();
    }

    private void showInvestmentTotalHigherThanAccountBalanceAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Investment Total Too High!");
        alert.setContentText("Investment total must be lower than account balance");
        alert.showAndWait();
    }

    private void showNoSelectedLoansAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("No Selected Loans For Investment!");
        alert.setContentText("At least 1 loan must be selected for investment to proceed");
        alert.showAndWait();
    }

    @FXML private void onInvestmentRequest(ActionEvent event) throws Exception{
        if(loanInvestmentTotalTextField.getText().isEmpty()) {
            showMissingInvestmentTotalAlert();
        }

        else {
            double loanInvestmentTotal = Double.parseDouble(loanInvestmentTotalTextField.getText());
            if (loanInvestmentTotal > userViewComponentController.getAccountBalance()) {
                showInvestmentTotalHigherThanAccountBalanceAlert();
            }

            else if (selectedLoanDetails.isEmpty()) {
                showNoSelectedLoansAlert();
            }

            else {
                String username = userViewComponentController.getAppController().getUsername();
                double minimumInterest = loanMinimumInterestTextField.getText().isEmpty() ? 0 : Double.parseDouble(loanMinimumInterestTextField.getText());
                InvestmentRequest investmentRequest = new InvestmentRequestBuilder(new HashSet<>(loanCategoryCheckListView.getCheckModel().getCheckedItems()), username, loanInvestmentTotal)
                        .setMinimumInterest(minimumInterest).setMaximumLoanOwnershipPercentage(loanMaxOwnershipRateSpinner.getValue())
                        .setMinimumLoanTerm(loanMinTermSpinner.getValue()).setMaximumBorrowerActiveLoans(borrowerMaxLoansSpinner.getValue())
                        .addLoansToInvestIn(selectedLoanDetails.stream().map(LoanDetails::getId).collect(Collectors.toSet())).build();
                String investmentRequestJSON = JsonUtil.GSON_INSTANCE.toJson(investmentRequest);
                RequestBody requestBody = RequestBody.create(investmentRequestJSON, MediaType.parse("application/json"));
                String finalUrl = HttpUrl.parse("http://localhost:8080/AlternaBank/invest")
                                .newBuilder().build().toString();
                Response response = HttpClientUtil.runPostSync(finalUrl, requestBody);
                if (response.code() != 200) {
                    showInvestmentTaskFailureAlert();
                }
                else {
                    investmentTaskFinished();
                }
;            }
        }
    }

    private void showInvestmentTaskFinishedMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Investment complete");
        alert.setHeaderText("Investment completed successfully!");
        alert.setContentText("The total funds that were requested to invest were divided between all of the selected loans according to the algorithm");
        alert.showAndWait();
    }

    private void showInvestmentTaskFailureAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Failed to execute investment request");
        alert.setContentText("There was an error while attempting to execute the investment request");
        alert.showAndWait();
    }

    private void investmentTaskFinished() {
        showInvestmentTaskFinishedMessage();
        selectedLoanDetails.clear();
    }
}
