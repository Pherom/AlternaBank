package com.alternabank.graphical.ui.application.user.payment.controls;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.user.payment.UserViewPaymentController;
import com.alternabank.graphical.ui.application.util.DoubleTextFormatter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentControlsViewController implements Initializable {

    @FXML
    private Button payAccumulatedDebtButton;

    @FXML
    private Button payRemainingTotalButton;

    @FXML
    private GridPane paymentControlsViewComponent;

    @FXML
    private VBox riskPaymentArea;

    @FXML
    private Button riskPaymentButton;

    @FXML
    private TextField riskPaymentTotalTextField;

    private UserViewPaymentController userViewPaymentComponentController;

    private ValidationSupport validationSupport = new ValidationSupport();

    public void setUserViewPaymentController(UserViewPaymentController controller) {
        this.userViewPaymentComponentController = controller;
    }

    public void onLoanSelected() {
        payAccumulatedDebtButton.textProperty().bind(Bindings.format("Pay accumulated debt (%.2f)", userViewPaymentComponentController.getSelectedLoanDetails().getAccumulatedDebtTotal()));
        payRemainingTotalButton.textProperty().bind(Bindings.format("Pay remaining total (%.2f)", userViewPaymentComponentController.getSelectedLoanDetails().getRemainingTotal()));
        riskPaymentArea.setDisable(userViewPaymentComponentController.getSelectedLoanDetails().getStatus() != Loan.Status.RISK);
        payAccumulatedDebtButton.setDisable(false);
        payRemainingTotalButton.setDisable(false);
        validationSupport.registerValidator(riskPaymentTotalTextField, true, Validator.createPredicateValidator((String text) -> text.isEmpty() || Double.parseDouble(text) <= userViewPaymentComponentController.getSelectedLoanDetails().getAccumulatedDebtTotal(), "Payment total must not be higher than accumulated debt"));
    }

    public void onLoanDeselected() {
        payAccumulatedDebtButton.textProperty().unbind();
        payRemainingTotalButton.textProperty().unbind();
        payAccumulatedDebtButton.setText("Pay accumulated debt (#)");
        payRemainingTotalButton.setText("Pay remaining total (#)");
        riskPaymentArea.setDisable(true);
        payAccumulatedDebtButton.setDisable(true);
        payRemainingTotalButton.setDisable(true);
    }

    private void showRiskPaymentTotalEmptyAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Payment Total Missing!");
        alert.setContentText("You must provide a payment total to proceed");
        alert.showAndWait();
    }

    private void shorRiskPaymentTotalHigherThanAccumulatedDebtAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Payment Total Higher Than Accumulated Debt!");
        alert.setContentText("Payment total cannot be higher than accumulated debt");
        alert.showAndWait();
    }

    @FXML
    public void onAccumulatedDebtPaymentRequest(ActionEvent event) {
        UserManager.getInstance().getAdmin().getLoanManager().getLoansByID().get(userViewPaymentComponentController.getSelectedLoanDetails().getId()).executeAccumulatedDebtPayment();
        userViewPaymentComponentController.getUserViewComponentController().getAppController().refreshCustomerAndLoanDetails();
    }

    @FXML
    public void onRemainingLoanTotalPaymentRequest(ActionEvent event) {
        UserManager.getInstance().getAdmin().getLoanManager().getLoansByID().get(userViewPaymentComponentController.getSelectedLoanDetails().getId()).executeRemainingTotalPayment();
        userViewPaymentComponentController.getUserViewComponentController().getAppController().refreshCustomerAndLoanDetails();
    }

    @FXML
    public void onRiskPaymentRequest(ActionEvent event) {
        if (riskPaymentTotalTextField.getText().isEmpty())
            showRiskPaymentTotalEmptyAlert();
        else if (Double.parseDouble(riskPaymentTotalTextField.getText()) > userViewPaymentComponentController.getSelectedLoanDetails().getAccumulatedDebtTotal()) {
            shorRiskPaymentTotalHigherThanAccumulatedDebtAlert();
        }
        else {
            UserManager.getInstance().getAdmin().getLoanManager().getLoansByID().get(userViewPaymentComponentController.getSelectedLoanDetails().getId()).executeRiskPayment(Double.parseDouble(riskPaymentTotalTextField.getText()));
            userViewPaymentComponentController.getUserViewComponentController().getAppController().refreshCustomerAndLoanDetails();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validationSupport.registerValidator(riskPaymentTotalTextField, true, Validator.createEmptyValidator("Payment total must not be empty"));
        riskPaymentTotalTextField.setTextFormatter(new DoubleTextFormatter(null));
    }
}
