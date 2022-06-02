package com.alternabank.graphical.ui.application.user.investment;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import com.alternabank.graphical.ui.application.util.DoubleTextFormatter;
import javafx.beans.binding.Bindings;
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

    @FXML private ProgressBar investmentTaskProgressBar;

    @FXML private Label investmentProgressLabel;

    @FXML private Label investmentProgressDescriptionLabel;

    @FXML private Button investButton;

    @FXML private HBox loanSelectionControlsArea;

    @FXML private VBox filtersAndInvestmentInfoArea;

    private int borrowerMaxLoansSpinnerMaxValue;

    private final ValidationSupport validationSupport = new ValidationSupport();

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        validationSupport.registerValidator(loanInvestmentTotalTextField, Validator.createPredicateValidator((String text) -> {
            Double textAsDouble = text.isEmpty() ? null : Double.parseDouble(text);
            return textAsDouble != null && textAsDouble > 0 && textAsDouble <= userViewComponentController.getAccountBalance();
        }, "Total investment must be higher than 0 and lower than account balance"));
        validationSupport.initInitialDecoration();
    }

    public void prepareForNewInvestmentRequest() {
        loanCategoryCheckListView.getCheckModel().checkAll();
        loanInvestmentTotalTextField.clear();
        loanMinimumInterestTextField.clear();
        loanMinTermSpinner.getValueFactory().setValue(1);
        borrowerMaxLoansSpinner.getValueFactory().setValue(borrowerMaxLoansSpinnerMaxValue);
        loanMaxOwnershipRateSpinner.getValueFactory().setValue(100);
        selectedLoanDetails.clear();
        investmentProgressDescriptionLabel.setText("");
        investmentProgressLabel.setText("#");
        investmentTaskProgressBar.setProgress(0);
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
    }

    private void updateMatchingLoansAccordingToFilter() {
        matchingLoanDetails.setAll(UserManager.getInstance().getAdmin().getLoanManager().getLoanDetails().stream().filter(loanDetails ->
                selectedLoanDetails.stream().noneMatch(selectedLoanDetails -> loanDetails.getId().equals(selectedLoanDetails.getId())) &&
                        !loanDetails.getBorrowerName().equals(userViewComponentController.getAppController().getSelectedUser().getName()) &&
                        loanDetails.getStatus() == Loan.Status.PENDING &&
                        loanCategoryCheckListView.getCheckModel().getCheckedItems().contains(loanDetails.getCategory()) &&
                        (loanMinimumInterestTextField.getText().isEmpty() || loanDetails.getTotalInterest() >= Double.parseDouble(loanMinimumInterestTextField.getText())) &&
                        loanDetails.getOriginalTerm() >= loanMinTermSpinner.getValue() &&
                        UserManager.getInstance().getAdmin().getCustomerManager().getCustomerDetailsByName(loanDetails.getBorrowerName()).getPostedLoanDetails().size() <= borrowerMaxLoansSpinner.getValue())
                .sorted(Comparator.comparing(LoanDetails::getId, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()));
    }

    private void onFilterUpdateRequest() {
        updateMatchingLoansAccordingToFilter();
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        loanCategoryCheckListView.getItems().setAll(userViewComponentController.getAppController().getAvailableLoanCategories());
        loanMinTermSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, userViewComponentController.getAppController().getLoanDetails().stream().mapToInt(loanDetails -> loanDetails.getOriginalTerm()).max().getAsInt(), 1));
        borrowerMaxLoansSpinnerMaxValue = UserManager.getInstance().getAdmin().getLoanManager().getPostedLoanCountOfCustomerWithMostPostedLoans();
        borrowerMaxLoansSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, borrowerMaxLoansSpinnerMaxValue, borrowerMaxLoansSpinnerMaxValue));
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

    @FXML private void onInvestmentRequest(ActionEvent event) {
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
                String currentUserName = UserManager.getInstance().getCurrentUser().getName();
                double minimumInterest = loanMinimumInterestTextField.getText().isEmpty() ? 0 : Double.parseDouble(loanMinimumInterestTextField.getText());
                UserManager.getInstance().getAdmin().getLoanManager().postInvestmentRequest(
                        investmentTask -> {
                            investmentTaskProgressBar.progressProperty().bind(investmentTask.progressProperty());
                            investmentProgressLabel.textProperty().bind(Bindings.format("%.0f%% :", Bindings.multiply(investmentTask.progressProperty(), 100)));
                            investmentProgressDescriptionLabel.textProperty().bind(investmentTask.messageProperty());
                            filtersAndInvestmentInfoArea.setDisable(true);
                            loanSelectionControlsArea.setDisable(true);
                            investButton.setDisable(true);
                            investmentTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                                investmentTaskFinished();
                            });
                        },
                        currentUserName, loanInvestmentTotal, minimumInterest, loanMaxOwnershipRateSpinner.getValue(), loanMinTermSpinner.getValue(),
                        borrowerMaxLoansSpinner.getValue(), new HashSet<>(loanCategoryCheckListView.getCheckModel().getCheckedItems()),
                        selectedLoanDetails.stream().map(LoanDetails::getId).collect(Collectors.toSet()));
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

    private void investmentTaskFinished() {
        showInvestmentTaskFinishedMessage();
        investmentTaskProgressBar.progressProperty().unbind();
        investmentProgressLabel.textProperty().unbind();
        investmentProgressDescriptionLabel.textProperty().unbind();
        selectedLoanDetails.clear();
        filtersAndInvestmentInfoArea.setDisable(false);
        loanSelectionControlsArea.setDisable(false);
        investButton.setDisable(false);
    }
}
