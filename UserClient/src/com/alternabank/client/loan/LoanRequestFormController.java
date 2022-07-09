package com.alternabank.client.loan;

import com.alternabank.client.app.AppController;
import com.alternabank.client.util.DoubleTextFormatter;
import com.alternabank.client.util.json.JsonUtil;
import com.alternabank.client.util.loan.LoanCategoriesUtil;
import com.alternabank.dto.loan.request.LoanRequest;
import com.alternabank.client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.*;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class LoanRequestFormController implements Initializable {

    private AppController appComponentController;

    private ValidationSupport validationSupport;

    @FXML
    private TextField loanCapitalTextField;

    @FXML
    private TextField loanCategoryTextField;

    @FXML
    private TextField loanIDTextField;

    @FXML
    private Spinner<Integer> loanInstallmentPeriodSpinner;

    @FXML
    private TextField loanInterestPerInstallmentTextField;

    @FXML
    private Spinner<Integer> loanTermSpinner;

    @FXML
    void loanRequestCancelled(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private void showLoanRequestSubmissionSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Success!");
        alert.setHeaderText("Loan posted successfully!");
        alert.setContentText("Your loan request was submitted and a respective loan was created successfully");
        alert.showAndWait();
    }

    private void showLoanRequestSubmissionFailureAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Failed to submit loan request");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void loanRequestSubmitted(ActionEvent event) throws Exception{
        if (validationSupport.isInvalid()) {
            StringBuilder errorMessageStringBuilder = new StringBuilder();
            validationSupport.getValidationResult().getErrors().forEach((validationMessage -> errorMessageStringBuilder.append(String.format("%s%s", validationMessage.getText(), System.lineSeparator()))));
            showLoanRequestSubmissionFailureAlert(errorMessageStringBuilder.toString());
        }
        else {
            LoanRequest loanRequest = LoanRequest.createByInterestPerPayment(appComponentController.getUsername(),
                    loanCategoryTextField.getText(), Double.parseDouble(loanCapitalTextField.getText()),
                    loanInstallmentPeriodSpinner.getValue(), Double.parseDouble(loanInterestPerInstallmentTextField.getText()),
                    loanTermSpinner.getValue(), loanIDTextField.getText());
            String loanRequestJSON = JsonUtil.GSON_INSTANCE.toJson(loanRequest);
            RequestBody requestBody = RequestBody.create(loanRequestJSON, MediaType.parse("application/json"));
            String finalUrl = HttpUrl
                    .parse("http://localhost:8080/AlternaBank/post-loan")
                    .newBuilder()
                    .build()
                    .toString();

            Response response = HttpClientUtil.runPostSync(finalUrl, requestBody);
            if (response.code() != 200) {
                showLoanRequestSubmissionFailureAlert("Unable to post a loan with the provided properties");
            }
            else {
                showLoanRequestSubmissionSuccessAlert();
                ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            }
        }
    }

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validationSupport = new ValidationSupport();
        loanCapitalTextField.setTextFormatter(new DoubleTextFormatter(0.0));
        validationSupport.registerValidator(loanCapitalTextField, Validator.createEmptyValidator("Missing loan capital"));
        validationSupport.registerValidator(loanCapitalTextField, (Control control, String newValue) -> ValidationResult.fromErrorIf(control, "Loan capital must be higher than 0", Double.parseDouble(newValue) <= 0));
        Platform.runLater(() -> TextFields.bindAutoCompletion(loanCategoryTextField, LoanCategoriesUtil.loanCategories));
        validationSupport.registerValidator(loanCategoryTextField, Validator.createEmptyValidator("Missing loan category"));
        validationSupport.registerValidator(loanIDTextField, Validator.createEmptyValidator("Missing loan ID"));
        loanTermSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        loanTermSpinner.valueProperty().addListener((observable, oldValue, newValue) -> validationSupport.revalidate());
        loanInstallmentPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        validationSupport.registerValidator(loanInstallmentPeriodSpinner.getEditor(), (Control control, String newValue) ->
            ValidationResult.fromErrorIf(control, "Loan installment period must be lower than or equal to loan term", Integer.parseInt(newValue) > loanTermSpinner.getValue()));
        validationSupport.registerValidator(loanInstallmentPeriodSpinner.getEditor(), (Control control, String newValue) -> ValidationResult.fromErrorIf(control, "Loan term must be divisible by loan installment period", loanTermSpinner.getValue() % Integer.parseInt(newValue) != 0));
        validationSupport.registerValidator(loanInterestPerInstallmentTextField, Validator.createEmptyValidator("Missing loan interest per installment"));
        validationSupport.registerValidator(loanInterestPerInstallmentTextField, Validator.createPredicateValidator((TextField loanInterestPerInstallment) -> Double.parseDouble(loanInterestPerInstallment.getText()) > 0, "Loan interest per installment must be higher than 0"));
    }
}
